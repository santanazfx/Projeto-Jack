package br.com.jack.pedidos.error;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
