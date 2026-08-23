package br.com.jack.pedidos.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
