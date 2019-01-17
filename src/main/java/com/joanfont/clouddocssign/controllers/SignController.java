package com.joanfont.clouddocssign.controllers;

import com.joanfont.clouddocssign.lib.PersistentFlashBag;
import com.joanfont.clouddocssign.trustedx.*;
import com.joanfont.clouddocssign.trustedx.entities.Identity;
import com.joanfont.clouddocssign.trustedx.entities.Token;
import com.joanfont.clouddocssign.trustedx.entities.TrustedXCredentials;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXIdentityTokenNotFoundException;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXTokenNotFoundException;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXUnauthorizedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/sign")
public class SignController {

    private TrustedXTokenRepository tokenRepository;

    private TrustedXOAuthClient client;

    private PersistentFlashBag flashBag;

    public SignController(
            TrustedXTokenRepository tokenRepository,
            TrustedXCredentials credentials,
            PersistentFlashBag flashbag
    ) {
        this.tokenRepository = tokenRepository;
        this.client = new TrustedXOAuthClient(credentials, tokenRepository);
        this.flashBag = flashbag;
    }

    @GetMapping("/{identityId}")
    public String sign(@PathVariable String identityId) {
        Identity identity = new Identity(identityId);
        try {
            Token token = this.tokenRepository.getIdentityToken(identity);
            System.out.println(token.getAuthorizationHeader());
            return "layout";
        } catch (TrustedXTokenNotFoundException ex) {
            return this.retirectToTokenRetrival("/sign/" + identityId, identity);
        }
    }

    private String retirectToTokenRetrival(String returnUrl, Identity identity) {
        TokenRequest tokenRequest = new IdentityTokenRequest(returnUrl, identity);
        this.flashBag.add("token_request", tokenRequest);
        return "forward:/trustedx/login";
    }
}
