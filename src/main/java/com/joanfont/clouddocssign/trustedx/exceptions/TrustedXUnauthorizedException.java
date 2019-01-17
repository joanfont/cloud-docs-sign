package com.joanfont.clouddocssign.trustedx.exceptions;

import com.joanfont.clouddocssign.trustedx.Scope;

public class TrustedXUnauthorizedException extends TrustedXErrorResponseException {

    private Scope scope;

    public TrustedXUnauthorizedException(String message) {
        super(message);
    }

    public TrustedXUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public Scope getScope() {
        return scope;
    }

    public TrustedXUnauthorizedException setScope(Scope scope) {
        this.scope = scope;
        return this;
    }
}
