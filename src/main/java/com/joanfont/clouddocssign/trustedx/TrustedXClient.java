package com.joanfont.clouddocssign.trustedx;

import com.joanfont.clouddocssign.lib.OkHttpClientFactory;
import com.joanfont.clouddocssign.trustedx.entities.Identity;
import com.joanfont.clouddocssign.trustedx.entities.SignedHash;
import com.joanfont.clouddocssign.trustedx.entities.Token;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXErrorResponseException;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXException;
import com.joanfont.clouddocssign.trustedx.requests.GetIdentityRequest;
import com.joanfont.clouddocssign.trustedx.requests.RegisterIdentityRequest;
import com.joanfont.clouddocssign.trustedx.requests.RemoveIdentityRequest;
import com.joanfont.clouddocssign.trustedx.requests.SignHashRequest;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrustedXClient {

    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;

    private static final String SCHEME = "https";
    private static final String HOST = "uoc.safelayer.com";
    private static final int PORT = 8082;

    private static final String LIST_IDENTITIES = "trustedx-resources/esigp/v1/sign_identities";
    private static final String REGISTER_IDENTITY = "trustedx-resources/esigp/v1/sign_identities/server/pki_x509/pkcs12";
    private static final String GET_IDENTITY = "trustedx-resources/esigp/v1/sign_identities/{identityId}";
    private static final String DELETE_IDENTITY = "trustedx-resources/esigp/v1/sign_identities/{identityId}";

    private static final String SIGN_HASH = "trustedx-resources/esigp/v1/signatures/server/raw";

    public TrustedXClient() {
        this.client = OkHttpClientFactory.unsafe();
    }

    public List<Identity> listIdentities(Token token) throws TrustedXException {
        URL requestUrl = this.buildUrl(LIST_IDENTITIES, null);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", token.getAuthorizationHeader());

        try {
            Response response = this.get(requestUrl, headers);
            JSONObject json = this.jsonFromResponse(response);
            JSONArray identities = json.getJSONArray("sign_identities");

            ArrayList<Identity> identityList = new ArrayList<>();

            for (Object identity : identities.toList()) {
                identityList.add(
                        Identity.fromMap((Map) identity)
                );
            }

            return identityList;
        } catch (TrustedXException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new TrustedXException(ex.getMessage(), ex.getCause());
        }
    }

    public Identity getIdentity(Token token, GetIdentityRequest request) throws TrustedXException {
        String identityId = request.getIdentity().getId();
        String getIdentityPath = GET_IDENTITY.replace("{identityId}", identityId);
        URL requestUrl = this.buildUrl(getIdentityPath, null);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", token.getAuthorizationHeader());

        try {
            Response response = this.get(requestUrl, headers);
            JSONObject json = this.jsonFromResponse(response);
            return Identity.fromMap(json.getJSONObject("identity").toMap());
        } catch (TrustedXException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new TrustedXException(ex.getMessage(), ex.getCause());
        }
    }

    public Identity registerIdentity(Token token, RegisterIdentityRequest request) throws TrustedXException {
        URL requestUrl = this.buildUrl(REGISTER_IDENTITY, null);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", token.getAuthorizationHeader());


        Identity identity = request.getIdentity();
        Map<String, Object> postParams = new HashMap<>();

        postParams.put("pkcs12", identity.getPkcs12());
        postParams.put("password", identity.getPassword());
        postParams.put("labels", identity.getLabels());

        Response response = this.postAsJSON(requestUrl, postParams, headers);

        try {
            JSONObject json = this.jsonFromResponse(response);
            String identityId = json.getJSONObject("identity").getString("id");
            identity.setId(identityId);
            return identity;
        } catch (Exception ex) {
            throw new TrustedXException(ex.getMessage(), ex.getCause());
        }
    }

    public void removeIdentity(Token token, RemoveIdentityRequest request) throws TrustedXException {
        String identityId = request.getIdentity().getId();
        String deleteIdentityPath = DELETE_IDENTITY.replace("{identityId}", identityId);

        URL requestUrl = this.buildUrl(deleteIdentityPath, null);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", token.getAuthorizationHeader());

        this.delete(requestUrl, headers);
    }

    public SignedHash signHash(Token token, SignHashRequest request) throws TrustedXException {
        URL requestUrl = this.buildUrl(SIGN_HASH, null);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", token.getAuthorizationHeader());

        Map<String, Object> postParams = new HashMap<>();
        postParams.put("digest_value", request.getDigestValue());
        postParams.put("signature_algorithm", request.getSignatureAlgorithm());
        postParams.put("sign_identity_id", request.getIdentity().getId());

        Response response = this.postAsJSON(requestUrl, postParams, headers);
        try {
            return new SignedHash(response.body().bytes());
        } catch (Exception ex) {
            throw new TrustedXException(ex.getMessage(), ex.getCause());
        }
    }

    Response get(URL url, Map<String, String> headers) throws TrustedXException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .get();

        if (headers != null) {
            this.addRequestHeaders(requestBuilder, headers);
        }

        Request request = requestBuilder.build();
        return this.getResponseFromRequest(request);
    }

    Response postAsFormData(URL url, Map<String, String> postParams, Map<String, String> headers) throws TrustedXException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        if (postParams != null) {
            this.addRequestBodyAsFormData(requestBuilder, postParams);
        }

        if (headers != null) {
            this.addRequestHeaders(requestBuilder, headers);
        }

        Request request = requestBuilder.build();

        return this.getResponseFromRequest(request);
    }

    Response postAsJSON(URL url, Map<String, Object> postParams, Map<String, String> headers) throws TrustedXException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        if (postParams != null) {
            this.addRequestBodyAsJSON(requestBuilder, postParams);
        }

        if (headers != null) {
            this.addRequestHeaders(requestBuilder, headers);
        }

        Request request = requestBuilder.build();
        return this.getResponseFromRequest(request);
    }


    Response delete(URL url, Map<String, String> headers) throws TrustedXException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .delete();

        if (headers != null) {
            this.addRequestHeaders(requestBuilder, headers);
        }

        Request request = requestBuilder.build();
        return this.getResponseFromRequest(request);
    }


    private Response getResponseFromRequest(Request request) throws TrustedXException {
        try {
            Response response =  this.client.newCall(request).execute();
            this.checkResponseErrors(response);
            return response;
        } catch (Exception ex) {
            throw new TrustedXException(ex.getMessage(), ex.getCause());
        }
    }

    private void checkResponseErrors(Response response) throws TrustedXException {
        if (!response.isSuccessful()) {
            try {
                String responseString = response.body() != null ? response.body().string() : "";
                TrustedXErrorResponseException ex = new TrustedXErrorResponseException(responseString);
                ex.setStatusCode(response.code());
                throw ex;
            } catch (Exception ex) {
                throw new TrustedXException(ex.getMessage(), ex.getCause());
            }
        }
    }


    private void addRequestBodyAsJSON(Request.Builder requestBuilder, Map<String, Object> postParams) {
        JSONObject jsonBody = new JSONObject(postParams);
        RequestBody requestBody = RequestBody.create(JSON, jsonBody.toString());
        requestBuilder.post(requestBody);
    }

    private void addRequestBodyAsFormData(Request.Builder requestBuilder, Map<String, String> postParams) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();

        for (Map.Entry<String, String> entry : postParams.entrySet()) {
            formBodyBuilder.add(entry.getKey(), entry.getValue());
        }

        RequestBody requestBody = formBodyBuilder.build();
        requestBuilder.post(requestBody);
    }

    private void addRequestHeaders(Request.Builder requestBuilder, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
        }
    }


    URL buildUrl(String path, Map<String, String> queryParams) {
        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme(SCHEME)
                .host(HOST)
                .port(PORT)
                .addPathSegments(path);

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addEncodedQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        return builder.build().url();
    }

    JSONObject jsonFromResponse(Response response) throws TrustedXException {
        try {
            String responseData = response.body().string();
            return new JSONObject(responseData);
        } catch (Exception ex) {
            throw new TrustedXException(ex.getMessage(), ex.getCause());
        }
    }
}
