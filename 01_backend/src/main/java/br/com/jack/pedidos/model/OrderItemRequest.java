package br.com.jack.pedidos.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderItemRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity,
        List<String> extras,
        List<String> removals,
        String notes
) { }
