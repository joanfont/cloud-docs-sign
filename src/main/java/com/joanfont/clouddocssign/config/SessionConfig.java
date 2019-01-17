package com.joanfont.clouddocssign.config;


import com.joanfont.clouddocssign.lib.PersistentFlashBag;
import com.joanfont.clouddocssign.lib.StorageProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class SessionConfig {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public PersistentFlashBag persistentFlashBag() {
        return new PersistentFlashBag();
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public StorageProvider storageProvider() {
        return new StorageProvider();
    }
}
