package com.vaticano.paroquia.domain.enums;

public enum Role {
    SUPER_ADMIN("super_admin"),
    SECRETARIO("secretario"),
    CHEFE_NUCLEO("chefe_nucleo");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role fromValue(String value) {
        for (Role role : Role.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }
}
