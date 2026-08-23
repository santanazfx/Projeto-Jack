package br.com.jack.pedidos.repository;

import br.com.jack.pedidos.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByTrackingCodeIgnoreCase(String trackingCode);
}
