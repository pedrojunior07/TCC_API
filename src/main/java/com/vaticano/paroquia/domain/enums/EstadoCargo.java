package com.vaticano.paroquia.domain.enums;

public enum EstadoCargo {
    ATIVO("ativo"),
    INATIVO("inativo");

    private final String value;

    EstadoCargo(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoCargo fromValue(String value) {
        for (EstadoCargo estado : EstadoCargo.values()) {
            if (estado.value.equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Invalid estado cargo: " + value);
    }
}
