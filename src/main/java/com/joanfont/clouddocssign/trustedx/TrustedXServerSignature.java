package com.joanfont.clouddocssign.trustedx;

import com.itextpdf.text.pdf.security.ExternalSignature;
import com.joanfont.clouddocssign.trustedx.entities.Identity;
import com.joanfont.clouddocssign.trustedx.entities.SignedHash;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXException;
import com.joanfont.clouddocssign.trustedx.requests.SignHashRequest;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TrustedXServerSignature implements ExternalSignature {

    private TrustedXOAuthClient client;

    private Identity identity;

    public TrustedXServerSignature(TrustedXOAuthClient client, Identity identity) {
        this.client = client;
        this.identity = identity;
    }

    @Override
    public String getHashAlgorithm() {
        return "SHA-256";
    }

    @Override
    public String getEncryptionAlgorithm() {
        return "RSA";
    }

    @Override
    public byte[] sign(byte[] message) throws GeneralSecurityException {
        byte[] hash = this.hashMessage(message);
        SignHashRequest request = this.getRequest(hash);
        try {
            SignedHash signedHash = client.signHash(request);
            return signedHash.getHash();
        } catch (TrustedXException e) {
            throw new GeneralSecurityException(e.getMessage(), e.getCause());
        }
    }

    private byte[] hashMessage(byte[] message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(this.getHashAlgorithm());
        return md.digest(message);
    }

    private SignHashRequest getRequest(byte[] message) {
        String digestValue = Base64.getEncoder().encodeToString(message);
        SignHashRequest request = new SignHashRequest();
        request.setIdentity(this.identity)
                .setDigestValue(digestValue)
                .setSignatureAlgorithm("rsa-sha256");

        return request;
    }
}
