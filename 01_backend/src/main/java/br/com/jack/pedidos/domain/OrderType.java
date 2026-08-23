package br.com.jack.pedidos.domain;

import br.com.jack.pedidos.error.BusinessException;

import java.util.Locale;

public enum OrderType {
    DELIVERY("Delivery"),
    PICKUP("Retirada"),
    TABLE("Mesa");

    private final String label;

    OrderType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static OrderType from(String value) {
        try {
            return value == null ? null : valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Tipo de atendimento inválido.");
        }
    }
}
