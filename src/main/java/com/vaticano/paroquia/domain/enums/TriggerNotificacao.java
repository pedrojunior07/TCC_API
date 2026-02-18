package com.vaticano.paroquia.domain.enums;

public enum TriggerNotificacao {
    MANUAL("manual"),
    VINTE_QUATRO_HORAS_ANTES("24h_antes"),
    DUAS_HORAS_ANTES("2h_antes"),
    APOS_CONFIRMACAO("apos_confirmacao");

    private final String value;

    TriggerNotificacao(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TriggerNotificacao fromValue(String value) {
        for (TriggerNotificacao trigger : TriggerNotificacao.values()) {
            if (trigger.value.equalsIgnoreCase(value)) {
                return trigger;
            }
        }
        throw new IllegalArgumentException("Invalid trigger notificacao: " + value);
    }
}
