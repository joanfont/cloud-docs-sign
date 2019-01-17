package com.joanfont.clouddocssign.security;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class GoogleUser {

    private Map<String, Object> data;

    private GoogleUser(Map<String, Object> data) {
        this.data = data;
    }

    public static GoogleUser fromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> details = null;

        if (auth.isAuthenticated()) {
            details = (Map<String, Object>) ((OAuth2User) auth.getPrincipal()).getAttributes();
        }

        return new GoogleUser(details);
    }

    public String getId() {
        return this.getDataString("sub");
    }

    public String getName() {
        return this.getDataString("name");
    }

    public String getPicture() {
        return this.getDataString("picture");
    }

    private String getDataString(String key) {
        return (String) this.data.get(key);
    }


}
