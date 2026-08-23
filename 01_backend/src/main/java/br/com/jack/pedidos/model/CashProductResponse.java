package br.com.jack.pedidos.model;

import java.math.BigDecimal;
import java.util.List;

public record CashProductResponse(Long id, String name, String description, String category, String bread, BigDecimal price, String image, boolean available, String unavailableReason, List<AddonOptionResponse> addons) { }
