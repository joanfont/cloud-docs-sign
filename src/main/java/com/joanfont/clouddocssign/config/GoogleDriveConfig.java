package com.joanfont.clouddocssign.config;

import com.google.api.services.drive.DriveScopes;
import com.joanfont.clouddocssign.storage.google.GoogleDriveConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GoogleDriveConfig {

    @Value("${storage.google-drive.client-id}")
    private String clientId;

    @Value("${storage.google-drive.client-secret}")
    private String clientSecret;

    @Bean
    public GoogleDriveConfiguration googleDriveConfiguration() {
        GoogleDriveConfiguration googleDriveConfiguration = new GoogleDriveConfiguration();
        googleDriveConfiguration
                .setClientId(this.clientId)
                .setClientSecret(this.clientSecret)
                .setScopes(new String[]{DriveScopes.DRIVE});

        return googleDriveConfiguration;
    }

}
