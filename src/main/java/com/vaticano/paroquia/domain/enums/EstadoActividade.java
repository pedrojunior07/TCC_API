package com.vaticano.paroquia.domain.enums;

public enum EstadoActividade {
    PLANEADA("planeada"),
    CONFIRMADA("confirmada"),
    REALIZADA("realizada"),
    CANCELADA("cancelada");

    private final String value;

    EstadoActividade(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoActividade fromValue(String value) {
        for (EstadoActividade estado : EstadoActividade.values()) {
            if (estado.value.equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Invalid estado actividade: " + value);
    }
}
