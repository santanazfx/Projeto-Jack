package br.com.jack.pedidos.model;

import jakarta.validation.constraints.Pattern;

public record FinalizeTabRequest(@Pattern(regexp = "CASH|PIX|DEBIT_CARD|CREDIT_CARD", message = "Forma de pagamento inválida.") String paymentMethod) { }
