package com.vaticano.paroquia.domain.enums;

public enum TipoCertificado {
    BATISMO("batismo"),
    CRISMA("crisma"),
    CASAMENTO("casamento"),
    DECLARACAO("declaracao");

    private final String value;

    TipoCertificado(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoCertificado fromValue(String value) {
        for (TipoCertificado tipo : TipoCertificado.values()) {
            if (tipo.value.equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Invalid tipo certificado: " + value);
    }
}
