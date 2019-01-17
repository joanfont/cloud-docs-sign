package com.joanfont.clouddocssign.config;

import com.joanfont.clouddocssign.storage.dropbox.DropboxConfiguration;
import com.joanfont.clouddocssign.storage.dropbox.DropboxSessionStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class DropboxConfig {

    @Value("${storage.dropbox.app-key}")
    private String appKey;

    @Value("${storage.dropbox.app-secret}")
    private String appSecret;

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public DropboxSessionStorage dropboxSettings() {
        return new DropboxSessionStorage();
    }

    @Bean
    public DropboxConfiguration dropboxConfiguration() {
        DropboxConfiguration dropboxConfiguration = new DropboxConfiguration();
        dropboxConfiguration
                .setAppKey(this.appKey)
                .setAppSecret(this.appSecret);

        return dropboxConfiguration;
    }
}
