package br.com.jack.pedidos.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false) private OrderEntity order;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false) private ProductEntity product;
    @Column(name = "product_name", nullable = false) private String productName;
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2) private BigDecimal unitPrice;
    @Column(nullable = false) private int quantity;
    @Column(name = "extras_snapshot", length = 1000) private String extrasSnapshot;
    @Column(name = "removals_snapshot", length = 1000) private String removalsSnapshot;
    @Column(length = 1000) private String notes;
    @Column(name = "line_total", nullable = false, precision = 12, scale = 2) private BigDecimal lineTotal;

    protected OrderItemEntity() { }
    public OrderItemEntity(ProductEntity product, String productName, BigDecimal unitPrice, int quantity, String extrasSnapshot, String removalsSnapshot, String notes) {
        this.product = product; this.productName = productName; this.unitPrice = unitPrice; this.quantity = quantity;
        this.extrasSnapshot = extrasSnapshot; this.removalsSnapshot = removalsSnapshot; this.notes = notes;
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    void setOrder(OrderEntity order) { this.order = order; }
    public BigDecimal getLineTotal() { return lineTotal; }
}
