package br.com.jack.pedidos.model;

import java.math.BigDecimal;

public record TabItemResponse(Long id, String productName, BigDecimal unitPrice, int quantity, String extras, String removals, String notes, BigDecimal total, String status) { }
