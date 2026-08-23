package br.com.jack.pedidos.repository;

import br.com.jack.pedidos.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByAvailableTrueOrderByDisplayOrderAsc();
    List<ProductEntity> findAllByOrderByDisplayOrderAsc();
}
