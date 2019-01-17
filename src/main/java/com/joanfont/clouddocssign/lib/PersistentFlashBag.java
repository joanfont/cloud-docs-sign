package com.joanfont.clouddocssign.lib;

import java.util.HashMap;
import java.util.Map;

public class PersistentFlashBag {

    private Map<String, Object> storage;

    public PersistentFlashBag() {
        this.storage = new HashMap<>();
    }

    public void add(String name, Object value) {
        this.storage.put(name, value);
    }

    public Object get(String name) {
        return this.storage.remove(name);
    }
}
