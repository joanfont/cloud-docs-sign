package com.joanfont.clouddocssign.trustedx.requests;

import com.joanfont.clouddocssign.trustedx.entities.Identity;

public class GetIdentityRequest extends Request {

    private Identity identity;

    public Identity getIdentity() {
        return identity;
    }

    public GetIdentityRequest setIdentity(Identity identity) {
        this.identity = identity;
        return this;
    }
}
