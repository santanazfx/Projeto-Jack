package br.com.jack.pedidos.domain;

public enum OrderStatus {
    RECEIVED,
    PREPARING,
    READY,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;

    public String label(OrderType type) {
        return switch (this) {
            case RECEIVED -> "Pedido recebido";
            case PREPARING -> "Em preparação";
            case READY -> type == OrderType.PICKUP ? "Pronto para retirada" : "Pedido pronto";
            case OUT_FOR_DELIVERY -> "Saiu para entrega";
            case DELIVERED -> type == OrderType.TABLE ? "Entregue à mesa" : "Entregue";
            case CANCELLED -> "Pedido cancelado";
        };
    }
}
