package com.vaticano.paroquia.domain.enums;

public enum EstadoVisita {
    PLANEADA("planeada"),
    REALIZADA("realizada"),
    CANCELADA("cancelada");

    private final String value;

    EstadoVisita(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoVisita fromValue(String value) {
        for (EstadoVisita estado : EstadoVisita.values()) {
            if (estado.value.equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Invalid estado visita: " + value);
    }
}
