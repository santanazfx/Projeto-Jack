package br.com.jack.pedidos.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tracking_code", nullable = false, unique = true) private String trackingCode;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false) private CustomerEntity customer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "address_id") private AddressEntity address;
    @Enumerated(EnumType.STRING) @Column(name = "order_type", nullable = false) private OrderType orderType;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OrderStatus status;
    @Enumerated(EnumType.STRING) @Column(name = "payment_method") private PaymentMethod paymentMethod;
    @Column(name = "table_number") private String tableNumber;
    @Column(name = "order_notes", length = 1000) private String orderNotes;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal subtotal;
    @Column(name = "delivery_fee", nullable = false, precision = 12, scale = 2) private BigDecimal deliveryFee;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal total;
    @Column(name = "estimated_time", nullable = false) private String estimatedTime;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) private List<OrderItemEntity> items = new ArrayList<>();
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) private List<OrderStatusHistoryEntity> statusHistory = new ArrayList<>();

    protected OrderEntity() { }
    public OrderEntity(String trackingCode, CustomerEntity customer, AddressEntity address, OrderType orderType, PaymentMethod paymentMethod,
                       String tableNumber, String orderNotes, BigDecimal subtotal, BigDecimal deliveryFee, String estimatedTime) {
        this.trackingCode = trackingCode; this.customer = customer; this.address = address; this.orderType = orderType;
        this.status = OrderStatus.RECEIVED; this.paymentMethod = paymentMethod; this.tableNumber = tableNumber; this.orderNotes = orderNotes;
        this.subtotal = subtotal; this.deliveryFee = deliveryFee; this.total = subtotal.add(deliveryFee); this.estimatedTime = estimatedTime;
    }
    @PrePersist void createTimestamps() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate void updateTimestamp() { updatedAt = LocalDateTime.now(); }
    public void addItem(OrderItemEntity item) { item.setOrder(this); items.add(item); }
    public void registerInitialStatus() { statusHistory.add(new OrderStatusHistoryEntity(this, null, status, "Sistema", "Pedido criado")); }
    public Long getId() { return id; }
    public String getTrackingCode() { return trackingCode; }
    public AddressEntity getAddress() { return address; }
    public OrderType getOrderType() { return orderType; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public String getEstimatedTime() { return estimatedTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getTableNumber() { return tableNumber; }
}
