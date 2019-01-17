package com.joanfont.clouddocssign.trustedx.exceptions;

import com.joanfont.clouddocssign.trustedx.Scope;
import com.joanfont.clouddocssign.trustedx.entities.Identity;

public class TrustedXTokenNotFoundException extends TrustedXException {

    protected Scope scope;

    public TrustedXTokenNotFoundException(Scope scope) {
        super("Token for scope " + scope + " not found");
        this.scope = scope;
    }

    public TrustedXTokenNotFoundException(String message) {
        super(message);
        this.scope = null;
    }

    public Scope getScope() {
        return this.scope;
    }

}
