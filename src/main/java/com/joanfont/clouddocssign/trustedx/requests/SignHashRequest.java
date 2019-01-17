package com.joanfont.clouddocssign.trustedx.requests;

import com.joanfont.clouddocssign.trustedx.entities.Identity;

public class SignHashRequest extends Request {

    private Identity identity;

    private String digestValue;

    private String signatureAlgorithm;

    public Identity getIdentity() {
        return this.identity;
    }

    public SignHashRequest setIdentity(Identity identity) {
        this.identity = identity;
        return this;
    }

    public String getDigestValue() {
        return this.digestValue;
    }

    public SignHashRequest setDigestValue(String digestValue) {
        this.digestValue = digestValue;
        return this;
    }

    public String getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    public SignHashRequest setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        return this;
    }
}
