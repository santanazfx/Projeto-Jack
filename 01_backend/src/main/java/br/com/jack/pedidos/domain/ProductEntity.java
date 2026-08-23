package br.com.jack.pedidos.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
public class ProductEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;
    @Column(nullable = false) private String name;
    @Column(nullable = false, length = 1000) private String description;
    @Column(nullable = false) private String bread;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    private String image;
    private String color;
    @Column(nullable = false) private boolean featured;
    @Column(nullable = false) private boolean available;
    @Column(name = "unavailable_reason") private String unavailableReason;
    @Column(name = "display_order", nullable = false) private int displayOrder;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_addons", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "addon_id"))
    private Set<AddonEntity> addons = new HashSet<>();

    public Long getId() { return id; }
    public CategoryEntity getCategory() { return category; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getBread() { return bread; }
    public BigDecimal getPrice() { return price; }
    public String getImage() { return image; }
    public String getColor() { return color; }
    public boolean isFeatured() { return featured; }
    public boolean isAvailable() { return available; }
    public int getDisplayOrder() { return displayOrder; }
    public Set<AddonEntity> getAddons() { return addons; }
    public String getUnavailableReason() { return unavailableReason; }
    public void setAvailability(boolean available, String reason) { this.available = available; this.unavailableReason = available ? null : reason; }
}
