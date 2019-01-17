package com.joanfont.clouddocssign.trustedx.entities;

public class SignedHash {

    private byte[] hash;

    public SignedHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getHash() {
        return hash;
    }

    public SignedHash setHash(byte[] hash) {
        this.hash = hash;
        return this;
    }
}
