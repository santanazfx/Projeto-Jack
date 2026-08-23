package br.com.jack.pedidos.model;

import jakarta.validation.constraints.NotNull;

public record AvailabilityRequest(@NotNull Boolean available, String reason) { }
