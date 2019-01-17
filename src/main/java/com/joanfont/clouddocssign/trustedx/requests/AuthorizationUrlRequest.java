package com.joanfont.clouddocssign.trustedx.requests;

import com.joanfont.clouddocssign.trustedx.Scope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthorizationUrlRequest {

    private static final String TOKEN_RESPONSE_TYPE = "code";
    private static final String ACR_VALUES = "urn:safelayer:tws:policies:authentication:flow:basic";

    private String redirectUrl;

    private List<Scope> scopes;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public AuthorizationUrlRequest setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public AuthorizationUrlRequest setScopes(List<Scope> scopes) {
        this.scopes = scopes;
        return this;
    }

    public Map<String, String> getQueryParameters() {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("redirect_uri", this.redirectUrl);
        parameters.put("response_type", TOKEN_RESPONSE_TYPE);
        parameters.put("acr_values", ACR_VALUES);

        String scopes = this.scopes
                .stream()
                .map(Scope::toString)
                .collect(Collectors.joining(" "));

        parameters.put("scope", scopes);

        return parameters;
    }
}
