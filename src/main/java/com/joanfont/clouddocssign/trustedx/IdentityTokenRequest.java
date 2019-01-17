package com.joanfont.clouddocssign.trustedx;

import com.joanfont.clouddocssign.trustedx.entities.Identity;

public class IdentityTokenRequest extends TokenRequest {

    private Identity identity;

    public IdentityTokenRequest(String returnUrl, Identity identity) {
        super(returnUrl, Scope.USE_IDENTITY);
        this.identity = identity;
    }

    public Identity getIdentity() {
        return identity;
    }
}
