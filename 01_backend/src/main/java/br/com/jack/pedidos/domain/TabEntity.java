package br.com.jack.pedidos.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tabs")
public class TabEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String code;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id") private CustomerEntity customer;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TabStatus status;
    @Column(name = "service_type", nullable = false) private String serviceType;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal subtotal = BigDecimal.ZERO;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal total = BigDecimal.ZERO;
    private String notes;
    @Column(name = "opened_at", nullable = false) private LocalDateTime openedAt;
    @Column(name = "closed_at") private LocalDateTime closedAt;
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "tab", cascade = CascadeType.ALL, orphanRemoval = true) private List<TabItemEntity> items = new ArrayList<>();
    protected TabEntity() { }
    public TabEntity(String code, CustomerEntity customer, String serviceType, String notes) { this.code=code; this.customer=customer; this.serviceType=serviceType; this.notes=notes; this.status=TabStatus.OPEN; }
    @PrePersist void created() { openedAt=LocalDateTime.now(); updatedAt=openedAt; }
    @PreUpdate void updated() { updatedAt=LocalDateTime.now(); }
    public void addItem(TabItemEntity item) { item.setTab(this); items.add(item); recalculate(); }
    public void removeItem(TabItemEntity item) { items.remove(item); recalculate(); }
    public void recalculate() { subtotal=items.stream().filter(TabItemEntity::isActive).map(TabItemEntity::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add); total=subtotal; }
    public void close() { status=TabStatus.CLOSED; closedAt=LocalDateTime.now(); }
    public void cancel() { status=TabStatus.CANCELLED; closedAt=LocalDateTime.now(); }
    public Long getId(){return id;} public String getCode(){return code;} public CustomerEntity getCustomer(){return customer;} public TabStatus getStatus(){return status;} public String getServiceType(){return serviceType;} public BigDecimal getSubtotal(){return subtotal;} public BigDecimal getTotal(){return total;} public String getNotes(){return notes;} public LocalDateTime getOpenedAt(){return openedAt;} public LocalDateTime getClosedAt(){return closedAt;} public List<TabItemEntity> getItems(){return items;}
}
