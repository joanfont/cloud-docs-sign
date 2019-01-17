package com.joanfont.clouddocssign.trustedx;

import com.joanfont.clouddocssign.trustedx.entities.SignedHash;
import com.joanfont.clouddocssign.trustedx.entities.TrustedXCredentials;
import com.joanfont.clouddocssign.trustedx.entities.Identity;
import com.joanfont.clouddocssign.trustedx.entities.Token;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXErrorResponseException;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXException;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXTokenNotFoundException;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXUnauthorizedException;
import com.joanfont.clouddocssign.trustedx.requests.*;
import okhttp3.Response;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrustedXOAuthClient {

    private TrustedXCredentials credentials;

    private TrustedXTokenRepository tokenRepository;

    private TrustedXClient client;

    private static final String AUTH_URL = "trustedx-authserver/main/oauth";
    private static final String TOKEN_URL = "trustedx-authserver/oauth/main/token";

    private static final int HTTP_401 = 401;


    public TrustedXOAuthClient(TrustedXCredentials credentials, TrustedXTokenRepository tokenRepository) {
        this.credentials = credentials;
        this.tokenRepository = tokenRepository;
        this.client = new TrustedXClient();
    }

    public List<Identity> listIdentities() throws TrustedXException {
        try {
            Token token = this.tokenRepository.get(Scope.MANAGE_IDENTITY);
            return this.client.listIdentities(token);
        } catch (TrustedXTokenNotFoundException ex) {
            throw ex;
        } catch (TrustedXErrorResponseException ex) {
            try {
                this.handleTrustedXErrorResponse(ex);
            } catch (TrustedXUnauthorizedException newEx) {
                newEx.setScope(Scope.MANAGE_IDENTITY);
                throw newEx;
            }
            throw ex;
        } catch (Exception ex) {
            throw new TrustedXException(ex.getMessage(), ex.getCause());
        }
    }

    public Identity getIdentity(GetIdentityRequest request) throws TrustedXException {
        try {
            Token token = this.tokenRepository.get(Scope.MANAGE_IDENTITY);
            return this.client.getIdentity(token, request);
        } catch (TrustedXTokenNotFoundException ex) {
            throw ex;
        } catch (TrustedXErrorResponseException ex) {
            try {
                this.handleTrustedXErrorResponse(ex);
            } catch (TrustedXUnauthorizedException newEx) {
                newEx.setScope(Scope.MANAGE_IDENTITY);
                throw newEx;
            }
            throw ex;
        } catch (Exception ex) {
            throw new TrustedXException(ex.getMessage(), ex.getCause());
        }
    }


    public Identity registerIdentity(RegisterIdentityRequest request) throws TrustedXException {
        try {
            Token token = this.tokenRepository.get(Scope.REGISTER_IDENTITY);
            return this.client.registerIdentity(token, request);
        } catch (TrustedXTokenNotFoundException ex) {
            throw ex;
        } catch (TrustedXErrorResponseException ex) {
            try {
                this.handleTrustedXErrorResponse(ex);
            } catch (TrustedXUnauthorizedException newEx) {
                newEx.setScope(Scope.REGISTER_IDENTITY);
                throw newEx;
            }
            throw ex;
        } catch (Exception ex) {
            throw new TrustedXException(ex.getMessage(), ex.getCause());
        }
    }

    public void removeIdentity(RemoveIdentityRequest request)  throws TrustedXException{
        try {
            Token token = this.tokenRepository.get(Scope.MANAGE_IDENTITY);
            this.client.removeIdentity(token, request);
        } catch (TrustedXErrorResponseException ex) {
            try {
                this.handleTrustedXErrorResponse(ex);
            } catch (TrustedXUnauthorizedException newEx) {
                newEx.setScope(Scope.MANAGE_IDENTITY);
                throw newEx;
            }
            throw ex;
        } catch (Exception ex) {
            throw new TrustedXException(ex.getMessage(), ex.getCause());
        }
    }

    public SignedHash signHash(SignHashRequest request) throws TrustedXException {
        try {
            Token token = this.tokenRepository.getIdentityToken(request.getIdentity());
            return this.client.signHash(token, request);
        } catch (TrustedXErrorResponseException ex) {
            try {
                this.handleTrustedXErrorResponse(ex);
            } catch (TrustedXUnauthorizedException newEx) {
                newEx.setScope(Scope.USE_IDENTITY);
                throw newEx;
            }
            throw ex;
        } catch (Exception ex) {
            throw new TrustedXException(ex.getMessage(), ex.getCause());
        }
    }

    public String getAuthorizationUrl(AuthorizationUrlRequest request) {
        Map<String, String> queryParams = request.getQueryParameters();
        queryParams.put("client_id", this.credentials.getClientId());

        URL url = this.client.buildUrl(AUTH_URL, queryParams);
        return url.toString();
    }

    public Token exchangeCodeForToken(CodeForTokenRequest request) throws TrustedXException {
        URL requestUrl = this.client.buildUrl(TOKEN_URL, null);

        Map<String, String> postParams = new HashMap<>();
        postParams.put("grant_type", "authorization_code");
        postParams.put("code", request.getCode());
        postParams.put("redirect_uri", request.getRedirectUrl());

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", this.credentials.getAsBasicAuthHeader());

        Response response = this.client.postAsFormData(requestUrl, postParams, headers);

        try {
            JSONObject jsonResponse = this.client.jsonFromResponse(response);
            return Token.fromJsonObject(jsonResponse);
        } catch (Exception ex) {
            throw new TrustedXException(ex.getMessage(), ex.getCause());
        }
    }

    private void handleTrustedXErrorResponse(TrustedXErrorResponseException exception) throws TrustedXException {
        if (exception.getStatusCode() == HTTP_401) {
            throw new TrustedXUnauthorizedException(exception.getMessage(), exception.getCause());
        } else {
            throw exception;
        }
    }
}
