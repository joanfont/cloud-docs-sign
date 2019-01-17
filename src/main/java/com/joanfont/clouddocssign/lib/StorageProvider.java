package com.joanfont.clouddocssign.lib;

import com.joanfont.clouddocssign.storage.Provider;

public class StorageProvider {

    public Provider provider;

    public StorageProvider() {

    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Provider getProvider() {
        return this.provider;
    }
}
