package com.joanfont.clouddocssign.trustedx;

public enum Scope {

    REGISTER_IDENTITY("urn:safelayer:eidas:sign:identity:register"),
    MANAGE_IDENTITY("urn:safelayer:eidas:sign:identity:manage"),
    USE_IDENTITY("urn:safelayer:eidas:sign:identity:use:server");

    private String value;

    Scope(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static Scope fromValue(String value) {
        for (Scope scope : Scope.values()) {
            if (scope.value.equalsIgnoreCase(value)) {
                return scope;
            }
        }
        return null;
    }

}
