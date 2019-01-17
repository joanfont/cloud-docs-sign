package com.joanfont.clouddocssign.trustedx.requests;

import com.joanfont.clouddocssign.trustedx.entities.Identity;

public class RegisterIdentityRequest extends Request {

    private Identity identity;

    public Identity getIdentity() {
        return this.identity;
    }

    public Request setIdentity(Identity identity) {
        this.identity = identity;
        return this;
    }
}
