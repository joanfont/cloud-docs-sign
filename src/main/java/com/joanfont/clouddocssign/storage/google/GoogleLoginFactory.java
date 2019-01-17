package com.joanfont.clouddocssign.storage.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;

import java.io.IOException;
import java.util.Arrays;


public class GoogleLoginFactory {


    public static GoogleAuthorizationCodeFlow build(GoogleDriveConfiguration googleDriveConfiguration) throws IOException {
       return new GoogleAuthorizationCodeFlow.Builder(
               new NetHttpTransport(),
               JacksonFactory.getDefaultInstance(),
               googleDriveConfiguration.getClientId(),
               googleDriveConfiguration.getClientSecret(),
               Arrays.asList(googleDriveConfiguration.getScopes())
       )
               .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
               .setAccessType("offline")
               .build();
    }
}


