package com.joanfont.clouddocssign.controllers;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.joanfont.clouddocssign.storage.google.GoogleDriveConfiguration;
import com.joanfont.clouddocssign.storage.google.GoogleLoginFactory;
import com.joanfont.clouddocssign.lib.StorageProvider;
import com.joanfont.clouddocssign.security.GoogleUser;
import com.joanfont.clouddocssign.storage.Provider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

@RequestMapping("/files/google")
@Controller
public class GoogleDriveLoginController {

    private static final String SUCCESS_URL = "/files/list";
    private static final String REDIRECT_URL = "/files/google/callback";

    private GoogleDriveConfiguration googleDriveConfiguration;

    private StorageProvider storageProvider;

    public GoogleDriveLoginController(
            GoogleDriveConfiguration googleDriveConfiguration,
            StorageProvider storageProvider
    ) {
        this.googleDriveConfiguration = googleDriveConfiguration;
        this.storageProvider = storageProvider;
    }

    @GetMapping("/login")
    public RedirectView login(HttpServletRequest request) throws Exception {
        String callbackUrl = this.getCallbackUrl(request);

        AuthorizationRequestUrl url = this.getGoogleAuthorizationCodeFlow().newAuthorizationUrl();
        url.setRedirectUri(callbackUrl);

        return new RedirectView(url.build());
    }

    @GetMapping("/callback")
    public RedirectView callback(HttpServletRequest request) throws Exception{

        String code = this.getResponseUrlCode(request);
        String redirectUri = this.getCallbackUrl(request);


        TokenResponse response = this.getGoogleAuthorizationCodeFlow()
                .newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();

        GoogleUser googleUser = GoogleUser.fromSecurityContext();
        String userId = googleUser.getId();
        GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = this.getGoogleAuthorizationCodeFlow();
        googleAuthorizationCodeFlow.createAndStoreCredential(response, userId);

        this.storageProvider.setProvider(Provider.GOOGLE_DRIVE);
        return new RedirectView(SUCCESS_URL);
    }

    private String getCallbackUrl(HttpServletRequest request) {
        GenericUrl genericUrl = new GenericUrl(request.getRequestURL().toString());
        genericUrl.setRawPath(REDIRECT_URL);
        return genericUrl.build();
    }

    private String getResponseUrlCode(HttpServletRequest request) {
        StringBuffer buf = request.getRequestURL();
        if (request.getQueryString() != null) {
            buf.append('?').append(request.getQueryString());
        }

        AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(buf.toString());
        return responseUrl.getCode();
    }

    private GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow() throws IOException {
        return GoogleLoginFactory.build(this.googleDriveConfiguration);
    }
}
