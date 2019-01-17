package com.joanfont.clouddocssign.trustedx.entities;

import com.joanfont.clouddocssign.trustedx.Scope;

public enum Role {

    STUDENT("student"),
    TEACHER("teacher");

    private String value;

    Role(String value) {
        this.value = value;
    }

    public String toLabel() {
        return String.format("role:%s", this.value);
    }

    public static Role fromValue(String value) {
        for (Role role : Role.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }

        return null;
    }

}
