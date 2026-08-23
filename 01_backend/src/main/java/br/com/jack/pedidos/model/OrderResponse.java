package br.com.jack.pedidos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        String trackingCode,
        String status,
        String estimatedTime,
        BigDecimal total,
        LocalDateTime createdAt,
        String serviceType,
        String tableNumber,
        String address
) { }
