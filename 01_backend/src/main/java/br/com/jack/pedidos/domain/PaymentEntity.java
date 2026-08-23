package br.com.jack.pedidos.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class PaymentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tab_id") private TabEntity tab;
    @Column(nullable = false) private String method;
    @Column(nullable = false) private String status;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal amount;
    @Column(name = "paid_at") private LocalDateTime paidAt;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    protected PaymentEntity() { }
    public PaymentEntity(TabEntity tab, String method, BigDecimal amount) { this.tab=tab; this.method=method; this.amount=amount; this.status="APPROVED"; this.paidAt=LocalDateTime.now(); }
    @PrePersist void created(){createdAt=LocalDateTime.now();}
}
