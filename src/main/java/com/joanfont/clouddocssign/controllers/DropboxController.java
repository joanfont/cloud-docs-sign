package com.joanfont.clouddocssign.controllers;

import com.dropbox.core.*;
import com.joanfont.clouddocssign.lib.StorageProvider;
import com.joanfont.clouddocssign.lib.UrlUtils;
import com.joanfont.clouddocssign.storage.Provider;
import com.joanfont.clouddocssign.storage.dropbox.DropboxConfiguration;
import com.joanfont.clouddocssign.storage.dropbox.DropboxSessionStorage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/files/dropbox")
public class DropboxController {

    private static final String SESSION_KEY = "dropbox-auth-csrf-token";
    private static final String CLIENT_IDENTIFIER = "CloudDocsSign";
    private static final String SUCCESS_URL = "/files/list";
    private static final String REDIRECT_URL = "/files/dropbox/callback";

    private DropboxSessionStorage dropboxSettings;

    private DropboxConfiguration dropboxConfiguration;

    private StorageProvider storageProvider;

    public DropboxController(
            DropboxSessionStorage dropboxSettings,
            DropboxConfiguration dropboxConfiguration,
            StorageProvider storageProvider)
    {
        this.dropboxSettings = dropboxSettings;
        this.dropboxConfiguration = dropboxConfiguration;
        this.storageProvider = storageProvider;
    }

    @GetMapping("/login")
    public RedirectView login(HttpServletRequest request) throws Exception {

        String redirectUrl = UrlUtils.urlWithPathFromUrl(
                request.getRequestURL().toString(),
                REDIRECT_URL
        );

        DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
                .withRedirectUri(redirectUrl, getSessionStore(request))
                .build();

        String authorizeUrl = getWebAuth(request).authorize(authRequest);

        return new RedirectView(authorizeUrl);
    }

    @GetMapping("/callback")
    public RedirectView callback(HttpServletRequest request) throws Exception {
        String redirectUrl = UrlUtils.urlWithPathFromUrl(
                request.getRequestURL().toString(),
                REDIRECT_URL
        );

        DbxAuthFinish authFinish = getWebAuth(request).finishFromRedirect(
                redirectUrl,
                getSessionStore(request),
                request.getParameterMap()
        );

        this.dropboxSettings.setAccessToken(authFinish.getAccessToken());
        this.storageProvider.setProvider(Provider.DROPBOX);
        return new RedirectView(SUCCESS_URL);
    }

    private DbxSessionStore getSessionStore(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        return new DbxStandardSessionStore(session, SESSION_KEY);
    }

    private DbxWebAuth getWebAuth(HttpServletRequest request) {
        DbxRequestConfig dbxRequestConfig = this.getRequestConfig(request);
        DbxAppInfo dbxAppInfo = this.getAppInfo();
        return new DbxWebAuth(dbxRequestConfig, dbxAppInfo);
    }

    private DbxRequestConfig getRequestConfig(HttpServletRequest request) {
        return DbxRequestConfig.newBuilder(CLIENT_IDENTIFIER)
                .withUserLocaleFrom(request.getLocale())
                .build();
    }

    private DbxAppInfo getAppInfo() {
        return new DbxAppInfo(
                this.dropboxConfiguration.getAppKey(),
                this.dropboxConfiguration.getAppSecret()
        );
    }
}
