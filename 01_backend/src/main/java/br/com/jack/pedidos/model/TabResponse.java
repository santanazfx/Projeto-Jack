package br.com.jack.pedidos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TabResponse(Long id, String code, String customerName, String phone, String serviceType, String status, LocalDateTime openedAt, LocalDateTime closedAt, int itemCount, BigDecimal subtotal, BigDecimal total, String notes, List<TabItemResponse> items) { }
