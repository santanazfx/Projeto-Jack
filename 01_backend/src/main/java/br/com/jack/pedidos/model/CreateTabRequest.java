package br.com.jack.pedidos.model;

import jakarta.validation.constraints.Pattern;

public record CreateTabRequest(
        String code,
        String customerName,
        String phone,
        String notes,
        @Pattern(regexp = "TABLE|DELIVERY|PICKUP", message = "Tipo de atendimento inválido.") String serviceType
) { }
