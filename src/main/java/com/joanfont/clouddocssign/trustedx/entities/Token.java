package com.joanfont.clouddocssign.trustedx.entities;

import com.joanfont.clouddocssign.trustedx.Scope;
import org.json.JSONObject;

public class Token {

    private String accessToken;

    private String tokenType;

    private int expiresIn;

    private Scope scope;

    private long createdAt;

    public Token(String accessToken, String tokenType, int expiresIn, Scope scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
    }

    public String getAuthorizationHeader() {
        return String.format("%s %s", this.tokenType, this.accessToken);
    }

    public Scope getScope() {
        return this.scope;
    }

    public boolean isExpired() {
        long currentTime = System.currentTimeMillis() / 1000;

        // give 3 minutes of margin
        long tokenExpiresAt = this.createdAt + this.expiresIn - (3 * 60);

        return currentTime > tokenExpiresAt;
    }

    public static Token fromJsonObject(JSONObject json) {
        String accessToken = json.getString("access_token");
        String tokenType = json.getString("token_type");
        int expiresIn = json.getInt("expires_in");
        Scope scope = Scope.fromValue(json.getString("scope"));

        Token token = new Token(accessToken, tokenType, expiresIn, scope);
        token.createdAt = (System.currentTimeMillis() / 1000);

        return token;
    }
}
