package com.joanfont.clouddocssign.config;


import com.joanfont.clouddocssign.trustedx.TrustedXTokenRepository;
import com.joanfont.clouddocssign.trustedx.entities.TrustedXCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class TrustedXConfig {

    @Value("${trustedx.client-id}")
    private String clientId;

    @Value("${trustedx.client-secret}")
    private String clientSecret;

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public TrustedXTokenRepository tokenRepository() {
        return new TrustedXTokenRepository();
    }

    @Bean
    public TrustedXCredentials trustedXCredentials() {
        return new TrustedXCredentials(this.clientId, this.clientSecret);
    }

}
