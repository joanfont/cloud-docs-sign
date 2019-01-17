package com.joanfont.clouddocssign.controllers;

import com.joanfont.clouddocssign.lib.PersistentFlashBag;
import com.joanfont.clouddocssign.lib.UrlUtils;
import com.joanfont.clouddocssign.trustedx.*;
import com.joanfont.clouddocssign.trustedx.entities.Identity;
import com.joanfont.clouddocssign.trustedx.entities.Token;
import com.joanfont.clouddocssign.trustedx.entities.TrustedXCredentials;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXException;
import com.joanfont.clouddocssign.trustedx.requests.AuthorizationUrlRequest;
import com.joanfont.clouddocssign.trustedx.requests.CodeForTokenRequest;
import com.joanfont.clouddocssign.trustedx.requests.SignHashAuthorizationUrlRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;


@Controller
@RequestMapping("/trustedx")
public class TrustedXController {

    private static final String REDIRECT_URL = "/trustedx/callback";

    private TrustedXTokenRepository tokenRepository;

    private TrustedXOAuthClient client;

    private PersistentFlashBag flashBag;

    public TrustedXController(
            TrustedXTokenRepository tokenRepository,
            TrustedXCredentials credentials,
            PersistentFlashBag flashBag
    ) {
        this.tokenRepository = tokenRepository;
        this.client = new TrustedXOAuthClient(credentials, tokenRepository);
        this.flashBag = flashBag;
    }

    @GetMapping("/login")
    public RedirectView login(HttpServletRequest request) {
        TokenRequest tokenRequest = (TokenRequest) this.flashBag.get("token_request");
        this.flashBag.add("token_request", tokenRequest);  // needed for the next request

        AuthorizationUrlRequest authorizationUrlRequest = this.getAuthorizationUrlRequest(request, tokenRequest);
        String authorizationUrl = this.client.getAuthorizationUrl(authorizationUrlRequest);

        return new RedirectView(authorizationUrl);
    }

    private AuthorizationUrlRequest getAuthorizationUrlRequest(HttpServletRequest request, TokenRequest tokenRequest) {
        AuthorizationUrlRequest authorizationUrlRequest;

        if (tokenRequest instanceof IdentityTokenRequest) {
            authorizationUrlRequest = new SignHashAuthorizationUrlRequest();
            Identity identity = ((IdentityTokenRequest) tokenRequest).getIdentity();
            ((SignHashAuthorizationUrlRequest) authorizationUrlRequest).setIdentity(identity);
        } else {
            authorizationUrlRequest = new AuthorizationUrlRequest();

        }

        String redirectUrl = UrlUtils.urlWithPathFromUrl(
                request.getRequestURL().toString(),
                REDIRECT_URL
        );

        authorizationUrlRequest.setRedirectUrl(redirectUrl)
                .setScopes(Collections.singletonList(tokenRequest.getScope()));

        return authorizationUrlRequest;
    }


    @GetMapping("/callback")
    public RedirectView callback(
            HttpServletRequest request,
            @RequestParam("code") String code
    ) throws TrustedXException
    {
        TokenRequest tokenRequest = (TokenRequest) this.flashBag.get("token_request");

        String redirectUrl = UrlUtils.urlWithPathFromUrl(
                request.getRequestURL().toString(),
                REDIRECT_URL
        );

        CodeForTokenRequest codeForTokenRequest = new CodeForTokenRequest();
        codeForTokenRequest
                .setRedirectUrl(redirectUrl)
                .setCode(code);

        Token token = this.client.exchangeCodeForToken(codeForTokenRequest);

        this.storeToken(tokenRequest, token);
        return new RedirectView(tokenRequest.getReturnUrl());
    }

    private void storeToken(TokenRequest tokenRequest, Token token) {
        if (tokenRequest instanceof IdentityTokenRequest) {
            this.tokenRepository.addIdentityToken(
                    ((IdentityTokenRequest) tokenRequest).getIdentity(),
                    token
            );
        } else {
            this.tokenRepository.add(token.getScope(), token);
        }
    }
}
