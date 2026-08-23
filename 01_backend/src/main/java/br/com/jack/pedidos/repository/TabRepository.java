package br.com.jack.pedidos.repository;

import br.com.jack.pedidos.domain.TabEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TabRepository extends JpaRepository<TabEntity, Long> {
    @EntityGraph(attributePaths = {"customer", "items"})
    List<TabEntity> findAllByOrderByOpenedAtDesc();
    @EntityGraph(attributePaths = {"customer", "items"})
    Optional<TabEntity> findDetailedById(Long id);
    boolean existsByCodeIgnoreCase(String code);
}
