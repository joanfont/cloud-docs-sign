package com.joanfont.clouddocssign.storage.dropbox;

public class DropboxSessionStorage {


    private String accessToken;

    String getAccessToken() {
        return this.accessToken;
    }

    public DropboxSessionStorage setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
