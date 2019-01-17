package com.joanfont.clouddocssign.controllers;

import com.dropbox.core.v2.DbxClientV2;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.joanfont.clouddocssign.storage.google.GoogleDriveConfiguration;
import com.joanfont.clouddocssign.storage.google.GoogleLoginFactory;
import com.joanfont.clouddocssign.file.DropboxFile;
import com.joanfont.clouddocssign.file.File;
import com.joanfont.clouddocssign.file.GoogleDriveFile;
import com.joanfont.clouddocssign.file.MimeType;
import com.joanfont.clouddocssign.lib.FileUtils;
import com.joanfont.clouddocssign.lib.PersistentFlashBag;
import com.joanfont.clouddocssign.lib.StorageProvider;
import com.joanfont.clouddocssign.security.GoogleUser;
import com.joanfont.clouddocssign.signer.PdfSigner;
import com.joanfont.clouddocssign.storage.Dropbox;
import com.joanfont.clouddocssign.storage.GoogleDrive;
import com.joanfont.clouddocssign.storage.Storage;
import com.joanfont.clouddocssign.storage.Provider;
import com.joanfont.clouddocssign.storage.dropbox.DropboxClientFactory;
import com.joanfont.clouddocssign.storage.dropbox.DropboxSessionStorage;
import com.joanfont.clouddocssign.trustedx.*;
import com.joanfont.clouddocssign.trustedx.entities.Identity;
import com.joanfont.clouddocssign.trustedx.entities.Token;
import com.joanfont.clouddocssign.trustedx.entities.TrustedXCredentials;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXTokenNotFoundException;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXUnauthorizedException;
import com.joanfont.clouddocssign.trustedx.requests.GetIdentityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@RequestMapping("/files")
@Controller
public class FilesController {

    private TrustedXOAuthClient client;
    private TrustedXTokenRepository tokenRepository;

    private PersistentFlashBag flashBag;

    private StorageProvider storageProvider;

    private GoogleDriveConfiguration googleDriveConfiguration;
    private DropboxSessionStorage dropboxSettings;
    
    @Autowired
    private HttpServletRequest request;

    public FilesController(
            TrustedXTokenRepository tokenRepository,
            TrustedXCredentials credentials,
            PersistentFlashBag flashbag,
            GoogleDriveConfiguration googleDriveConfiguration,
            StorageProvider storageProvider,
            DropboxSessionStorage dropboxSettings
    ) {
        this.tokenRepository = tokenRepository;
        this.client = new TrustedXOAuthClient(credentials, tokenRepository);

        this.flashBag = flashbag;
        this.storageProvider = storageProvider;
        this.googleDriveConfiguration = googleDriveConfiguration;
        this.dropboxSettings = dropboxSettings;
    }

    @GetMapping
    public String chooseProvider(Model model) throws Exception {
        return "files/providers";
    }

    @GetMapping("/list")
    public String list(Model model) throws Exception {
        Storage provider = this.getStorageProvider();

        List<File> files = provider.listFiles();
        String iconClass = this.resolveIconClass();

        model.addAttribute("files", files);
        model.addAttribute("icon", iconClass);

        return "files/list";
    }

    @GetMapping(value = "/{id}/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] download(@PathVariable String id) throws Exception {

        Storage provider = this.getStorageProvider();

        File file = provider.newFile();
        file.setId(id);

        ByteArrayOutputStream bytes = provider.download(file);
        return bytes.toByteArray();

    }

    @GetMapping("/{id}/sign")
    public String sign(HttpServletRequest request, @PathVariable String id, Model model) throws Exception {

        try {
            List<Identity> identities = this.client.listIdentities();
            model.addAttribute("identities", identities);
        } catch (TrustedXUnauthorizedException | TrustedXTokenNotFoundException ex) {
            return this.redirectToTrustedXTokenRetrival("/files/" + id + "/sign", Scope.MANAGE_IDENTITY);
        }

        Storage provider = this.getStorageProvider();

        File file = provider.get(id);
        model.addAttribute("file", file);

        return "files/sign";
    }

    @GetMapping("/{fileId}/sign/{identityId}")
    public String doSign(
            HttpServletRequest request,
            @PathVariable String fileId,
            @PathVariable String identityId
    ) throws Exception {
        Identity tmpIdentity = new Identity(identityId);
        String redirect;
        String returnUrl = "/files/" + fileId + "/sign/" + identityId;

        redirect = this.loadSigningTokenOrRedirect(returnUrl, tmpIdentity);
        if (redirect != null) {
            return redirect;
        }

        redirect = this.loadManageIdentitiesTokenOrRedirect(returnUrl);
        if (redirect != null) {
            return redirect;
        }

        Storage storage = this.getStorageProvider();

        File originalFile = storage.get(fileId);
        ByteArrayOutputStream originalFileBytes = storage.download(originalFile);
        ByteArrayInputStream originalPdf = new ByteArrayInputStream(originalFileBytes.toByteArray());

        GetIdentityRequest getIdentityRequest = new GetIdentityRequest();
        getIdentityRequest.setIdentity(tmpIdentity);

        Identity identity = this.client.getIdentity(getIdentityRequest);

        PdfSigner pdfSigner = new PdfSigner(this.client);
        ByteArrayInputStream signedPdf = new ByteArrayInputStream(pdfSigner.sign(originalPdf, identity).toByteArray());

        File uploadFile = this.getFileToUpload(originalFile);
        storage.upload(uploadFile, signedPdf);

        return "redirect:/files/list";
    }


    private File getFileToUpload(File originalFile) {
        File fileToUpload = null;

        if (this.storageProvider.getProvider() == Provider.GOOGLE_DRIVE) {
            fileToUpload = new GoogleDriveFile();
            this.doGoogleDriveFileUploadStuff(originalFile, fileToUpload);
        } else if (this.storageProvider.getProvider() == Provider.DROPBOX) {
            fileToUpload = new DropboxFile();
            this.doDropboxFileUploadStuff(originalFile, fileToUpload);
        }

        return fileToUpload;
    }

    private void doGoogleDriveFileUploadStuff(File originalFile, File uploadFile) {
        String newFileName = FileUtils.appendToFileName(originalFile.getNameToUpload(), "_signed");
        uploadFile.setNameToUpload(newFileName);
        uploadFile.setMimeType(MimeType.PDF);
    }

    private void doDropboxFileUploadStuff(File originalFile, File uploadFile) {
        String newFilePath = FileUtils.appendToFileName(originalFile.getNameToUpload(), "_signed");
        uploadFile.setNameToUpload(newFilePath);
    }

    private Storage getStorageProvider() throws Exception {
        Storage storage = null;

        if (this.storageProvider.getProvider() == Provider.GOOGLE_DRIVE) {
            storage = this.getGoogleStorage();
        } else if (this.storageProvider.getProvider() == Provider.DROPBOX) {
            storage = this.getDropboxStorage();
        }

        return storage;
    }

    private Storage getGoogleStorage() throws Exception {
        GoogleUser googleUser = this.getGoogleUser();
        GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = GoogleLoginFactory.build(this.googleDriveConfiguration);
        Credential credential =googleAuthorizationCodeFlow.loadCredential(googleUser.getId());
        return new GoogleDrive(credential);
    }

    private Storage getDropboxStorage() throws Exception {
        DbxClientV2 client = DropboxClientFactory.build(this.dropboxSettings);
        return new Dropbox(client);
    }

    private String loadSigningTokenOrRedirect(String returnUrl, Identity identity) {
        try {
            Token token = this.tokenRepository.getIdentityToken(identity);
            if (token.isExpired()) {
                return this.redirectToTrustedXIdentityTokenRetrival(
                        returnUrl,
                        identity
                );
            }

            return null;
        } catch (TrustedXTokenNotFoundException e) {
            return this.redirectToTrustedXIdentityTokenRetrival(
                    returnUrl,
                    identity
            );
        }
    }

    private String loadManageIdentitiesTokenOrRedirect(String returnUrl) {
        try {
            Token token = this.tokenRepository.get(Scope.MANAGE_IDENTITY);
            if (token.isExpired()) {
                return this.redirectToTrustedXTokenRetrival(
                        returnUrl,
                        Scope.MANAGE_IDENTITY
                );
            }

            return null;
        } catch (TrustedXTokenNotFoundException e) {
            return this.redirectToTrustedXTokenRetrival(
                    returnUrl,
                    Scope.MANAGE_IDENTITY
            );
        }
    }

    private String redirectToTrustedXTokenRetrival(String returnUrl, Scope scope) {
        TokenRequest tokenRequest = new TokenRequest(returnUrl, scope);
        this.flashBag.add("token_request", tokenRequest);
        return "forward:/trustedx/login";
    }

    private String redirectToTrustedXIdentityTokenRetrival(String returnUrl, Identity identity) {
        IdentityTokenRequest tokenRequest = new IdentityTokenRequest(returnUrl, identity);
        this.flashBag.add("token_request", tokenRequest);
        return "forward:/trustedx/login";
    }

    private String resolveIconClass() {
        if (this.storageProvider.getProvider() == Provider.GOOGLE_DRIVE) {
            return "zmdi-google-drive";
        } else if (this.storageProvider.getProvider() == Provider.DROPBOX) {
            return "zmdi-dropbox";
        } else {
            return "zmdi-file";
        }
    }

    private GoogleUser getGoogleUser() {
        return GoogleUser.fromSecurityContext();
    }
}
