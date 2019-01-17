package com.joanfont.clouddocssign.storage.dropbox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxClientFactory {

    private static final String CLIENT_IDENTIFIER = "CloudDocsSign";

    public static DbxClientV2 build(DropboxSessionStorage dropboxSessionStorage) {
        return new DbxClientV2(
                getRequestConfig(),
                dropboxSessionStorage.getAccessToken()
        );
    }

    private static DbxRequestConfig getRequestConfig() {
        return DbxRequestConfig.newBuilder(CLIENT_IDENTIFIER)
                .build();
    }

}
