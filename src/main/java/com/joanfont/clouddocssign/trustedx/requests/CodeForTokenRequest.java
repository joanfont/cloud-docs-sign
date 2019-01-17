package com.joanfont.clouddocssign.trustedx.requests;

public class CodeForTokenRequest extends Request {

    private String redirectUrl;

    private String code;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public CodeForTokenRequest setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public String getCode() {
        return code;
    }

    public CodeForTokenRequest setCode(String code) {
        this.code = code;
        return this;
    }
}
