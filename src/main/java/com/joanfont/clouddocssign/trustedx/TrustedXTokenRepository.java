package com.joanfont.clouddocssign.trustedx;

import com.joanfont.clouddocssign.trustedx.entities.Identity;
import com.joanfont.clouddocssign.trustedx.entities.Token;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXIdentityTokenNotFoundException;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXTokenNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class TrustedXTokenRepository {

    private Map<Scope, Token> tokenStorage;

    private Map<String, Token> identityTokenStorage;

    public TrustedXTokenRepository() {
        this.tokenStorage = new HashMap<>();
        this.identityTokenStorage = new HashMap<>();
    }

    public void add(Scope scope, Token token) {
        this.tokenStorage.put(scope, token);
    }

    public Token get(Scope scope) throws TrustedXTokenNotFoundException {
        Token token = this.tokenStorage.get(scope);

        if (token == null) {
            throw new TrustedXTokenNotFoundException(scope);
        }

        return token;
    }

    public void addIdentityToken(Identity identity, Token token) {
        this.identityTokenStorage.put(identity.getId(), token);
    }

    public Token getIdentityToken(Identity identity) throws TrustedXTokenNotFoundException {
        Token token = this.identityTokenStorage.get(identity.getId());

        if (token == null) {
            throw new TrustedXIdentityTokenNotFoundException(identity);
        }

        return token;
    }
}
