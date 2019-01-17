package com.joanfont.clouddocssign.trustedx.requests;

import com.joanfont.clouddocssign.trustedx.entities.Identity;

public class RemoveIdentityRequest {

    private Identity identity;

    public Identity getIdentity() {
        return identity;
    }

    public RemoveIdentityRequest setIdentity(Identity identity) {
        this.identity = identity;
        return this;
    }
}
