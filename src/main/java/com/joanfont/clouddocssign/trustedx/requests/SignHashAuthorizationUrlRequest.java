package com.joanfont.clouddocssign.trustedx.requests;

import com.joanfont.clouddocssign.trustedx.entities.Identity;

import java.util.Map;

public class SignHashAuthorizationUrlRequest extends AuthorizationUrlRequest {

    private Identity identity;

    public Identity getIdentity() {
        return identity;
    }

    public SignHashAuthorizationUrlRequest setIdentity(Identity identity) {
        this.identity = identity;
        return this;
    }

    @Override
    public Map<String, String> getQueryParameters() {
        Map<String, String> parameters = super.getQueryParameters();
        parameters.put("sign_identity_id", this.identity.getId());
        return parameters;
    }
}
