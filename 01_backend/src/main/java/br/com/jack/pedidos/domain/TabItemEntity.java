package br.com.jack.pedidos.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="tab_items")
public class TabItemEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tab_id", nullable=false) private TabEntity tab;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id", nullable=false) private ProductEntity product;
    @Column(name="product_name", nullable=false) private String productName;
    @Column(name="unit_price",nullable=false,precision=12,scale=2) private BigDecimal unitPrice;
    @Column(nullable=false) private int quantity;
    @Column(name="extras_snapshot") private String extrasSnapshot;
    @Column(name="removals_snapshot") private String removalsSnapshot;
    private String notes;
    @Column(name="line_total",nullable=false,precision=12,scale=2) private BigDecimal lineTotal;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TabItemStatus status=TabItemStatus.ACTIVE;
    @Column(name="cancelled_at") private LocalDateTime cancelledAt;
    @Column(name="cancellation_reason") private String cancellationReason;
    protected TabItemEntity() { }
    public TabItemEntity(ProductEntity product, BigDecimal unitPrice, int quantity, String extras, String removals, String notes) { this.product=product; productName=product.getName(); this.unitPrice=unitPrice; this.quantity=quantity; extrasSnapshot=extras; removalsSnapshot=removals; this.notes=notes; lineTotal=unitPrice.multiply(BigDecimal.valueOf(quantity)); }
    void setTab(TabEntity tab){this.tab=tab;} public void update(int quantity,String notes){this.quantity=quantity;this.notes=notes;this.lineTotal=unitPrice.multiply(BigDecimal.valueOf(quantity));} public void cancel(String reason){status=TabItemStatus.CANCELLED; cancelledAt=LocalDateTime.now(); cancellationReason=reason;}
    public Long getId(){return id;} public TabEntity getTab(){return tab;} public String getProductName(){return productName;} public BigDecimal getUnitPrice(){return unitPrice;} public int getQuantity(){return quantity;} public String getExtrasSnapshot(){return extrasSnapshot;} public String getRemovalsSnapshot(){return removalsSnapshot;} public String getNotes(){return notes;} public BigDecimal getLineTotal(){return lineTotal;} public TabItemStatus getStatus(){return status;} public boolean isActive(){return status==TabItemStatus.ACTIVE;}
}
