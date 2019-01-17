package com.joanfont.clouddocssign.trustedx;

public class TokenRequest {

    private String returnUrl;

    private Scope scope;

    public TokenRequest(String returnUrl, Scope scope) {
        this.returnUrl = returnUrl;
        this.scope = scope;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public Scope getScope() {
        return scope;
    }
}
