package br.com.jack.pedidos.model;

import java.math.BigDecimal;

public record Product(
        Long id,
        String name,
        String description,
        String category,
        String bread,
        BigDecimal price,
        String image,
        String color,
        boolean featured,
        boolean available
) { }
