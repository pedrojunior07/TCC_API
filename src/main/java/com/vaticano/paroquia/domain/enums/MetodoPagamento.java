package com.vaticano.paroquia.domain.enums;

public enum MetodoPagamento {
    NUMERARIO("numerario"),
    MPESA("mpesa"),
    EMOLA("emola"),
    TRANSFERENCIA("transferencia"),
    OUTRO("outro");

    private final String value;

    MetodoPagamento(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MetodoPagamento fromValue(String value) {
        for (MetodoPagamento metodo : MetodoPagamento.values()) {
            if (metodo.value.equalsIgnoreCase(value)) {
                return metodo;
            }
        }
        throw new IllegalArgumentException("Invalid metodo pagamento: " + value);
    }
}
