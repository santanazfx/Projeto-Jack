package br.com.jack.pedidos.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateTabItemRequest(@NotNull @Min(1) Integer quantity, String notes) { }
