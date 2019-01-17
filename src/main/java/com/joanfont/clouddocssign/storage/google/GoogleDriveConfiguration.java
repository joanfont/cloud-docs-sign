package com.joanfont.clouddocssign.storage.google;

import java.util.List;

public class GoogleDriveConfiguration {

    private String clientId;

    private String clientSecret;

    private String[] scopes;

    public String getClientId() {
        return clientId;
    }

    public GoogleDriveConfiguration setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public GoogleDriveConfiguration setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public String[] getScopes() {
        return scopes;
    }

    public GoogleDriveConfiguration setScopes(String[] scopes) {
        this.scopes = scopes;
        return this;
    }
}
