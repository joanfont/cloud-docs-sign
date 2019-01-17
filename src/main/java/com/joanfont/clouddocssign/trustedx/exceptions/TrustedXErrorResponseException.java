package com.joanfont.clouddocssign.trustedx.exceptions;

public class TrustedXErrorResponseException extends TrustedXException {

    private int statusCode;

    public TrustedXErrorResponseException(String message) {
        super(message);
    }

    public TrustedXErrorResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public TrustedXErrorResponseException setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }
}
