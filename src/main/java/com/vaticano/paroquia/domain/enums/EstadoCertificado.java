package com.vaticano.paroquia.domain.enums;

public enum EstadoCertificado {
    PENDENTE("pendente"),
    APROVADO("aprovado"),
    RECUSADO("recusado"),
    EMITIDO("emitido");

    private final String value;

    EstadoCertificado(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoCertificado fromValue(String value) {
        for (EstadoCertificado estado : EstadoCertificado.values()) {
            if (estado.value.equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Invalid estado certificado: " + value);
    }
}
