package br.com.jack.pedidos.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history")
public class OrderStatusHistoryEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false) private OrderEntity order;
    @Enumerated(EnumType.STRING) @Column(name = "previous_status") private OrderStatus previousStatus;
    @Enumerated(EnumType.STRING) @Column(name = "current_status", nullable = false) private OrderStatus currentStatus;
    @Column(name = "changed_by") private String changedBy;
    @Column(length = 1000) private String note;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;

    protected OrderStatusHistoryEntity() { }
    OrderStatusHistoryEntity(OrderEntity order, OrderStatus previousStatus, OrderStatus currentStatus, String changedBy, String note) {
        this.order = order; this.previousStatus = previousStatus; this.currentStatus = currentStatus; this.changedBy = changedBy; this.note = note;
    }
    @PrePersist void timestamp() { createdAt = LocalDateTime.now(); }
}
