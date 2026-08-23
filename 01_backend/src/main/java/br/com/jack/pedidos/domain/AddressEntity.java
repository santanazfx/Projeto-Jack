package br.com.jack.pedidos.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class AddressEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;
    @Column(name = "zip_code", nullable = false) private String zipCode;
    @Column(nullable = false) private String street;
    @Column(nullable = false) private String number;
    private String complement;
    @Column(nullable = false) private String neighborhood;
    private String city;
    private String state;
    private String reference;

    protected AddressEntity() { }
    public AddressEntity(CustomerEntity customer, String zipCode, String street, String number, String complement, String neighborhood, String city, String state, String reference) {
        this.customer = customer; this.zipCode = zipCode; this.street = street; this.number = number; this.complement = complement;
        this.neighborhood = neighborhood; this.city = city; this.state = state; this.reference = reference;
    }
    public String display() {
        String suffix = complement == null || complement.isBlank() ? "" : ", " + complement;
        String referenceText = reference == null || reference.isBlank() ? "" : " — " + reference;
        return street + ", " + number + suffix + " - " + neighborhood + referenceText;
    }
}
