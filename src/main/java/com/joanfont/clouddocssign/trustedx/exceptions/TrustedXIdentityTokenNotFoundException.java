package com.joanfont.clouddocssign.trustedx.exceptions;

import com.joanfont.clouddocssign.trustedx.Scope;
import com.joanfont.clouddocssign.trustedx.entities.Identity;

public class TrustedXIdentityTokenNotFoundException extends TrustedXTokenNotFoundException {

    public TrustedXIdentityTokenNotFoundException(Identity identity) {
        super("Token for identity " + identity.getId() + " not found");
        this.scope = Scope.USE_IDENTITY;
    }
}
