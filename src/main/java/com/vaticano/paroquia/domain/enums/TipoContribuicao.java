package com.vaticano.paroquia.domain.enums;

public enum TipoContribuicao {
    COTA("cota"),
    CONTRIBUICAO("contribuicao"),
    DOACAO("doacao"),
    OUTRO("outro");

    private final String value;

    TipoContribuicao(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoContribuicao fromValue(String value) {
        for (TipoContribuicao tipo : TipoContribuicao.values()) {
            if (tipo.value.equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Invalid tipo contribuicao: " + value);
    }
}
