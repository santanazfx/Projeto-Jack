package br.com.jack.pedidos.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class CustomerEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(unique = true) private String phone;
    private String email;
    private String cpf;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;

    protected CustomerEntity() { }
    public CustomerEntity(String name, String phone) { this.name = name; this.phone = phone; }
    @PrePersist void createTimestamps() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate void updateTimestamp() { updatedAt = LocalDateTime.now(); }
    public void updateName(String value) { this.name = value; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
}
