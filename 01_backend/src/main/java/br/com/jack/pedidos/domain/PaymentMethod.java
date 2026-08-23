package br.com.jack.pedidos.domain;

import java.util.Locale;

public enum PaymentMethod {
    CASH,
    CARD,
    PIX;

    public static PaymentMethod from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "DINHEIRO", "CASH" -> CASH;
            case "CARTÃO", "CARTAO", "CARD" -> CARD;
            case "PIX" -> PIX;
            default -> throw new IllegalArgumentException("Forma de pagamento inválida.");
        };
    }
}
