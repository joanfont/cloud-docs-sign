package com.joanfont.clouddocssign.trustedx.entities;

import java.util.Base64;

public class TrustedXCredentials {

    private String clientId;

    private String clientSecret;

    public TrustedXCredentials(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public TrustedXCredentials setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public TrustedXCredentials setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public String getAsBasicAuthHeader() {
        String authHeader = Base64.getEncoder().encodeToString(
                String.format("%s:%s", this.clientId, this.clientSecret).getBytes()
        );

        return String.format("Basic %s", authHeader);
    }
}
