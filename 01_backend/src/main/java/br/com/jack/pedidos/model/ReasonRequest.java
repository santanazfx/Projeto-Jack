package br.com.jack.pedidos.model;

import jakarta.validation.constraints.NotBlank;

public record ReasonRequest(@NotBlank(message = "Informe o motivo.") String reason) { }
