package com.joanfont.clouddocssign.storage.dropbox;

public class DropboxConfiguration {

    private String appKey;

    private String appSecret;

    public String getAppKey() {
        return appKey;
    }

    public DropboxConfiguration setAppKey(String appKey) {
        this.appKey = appKey;
        return this;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public DropboxConfiguration setAppSecret(String appSecret) {
        this.appSecret = appSecret;
        return this;
    }
}
