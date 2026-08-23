package br.com.jack.pedidos.error;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(String code, String message, Map<String, String> fields, LocalDateTime timestamp) { }
