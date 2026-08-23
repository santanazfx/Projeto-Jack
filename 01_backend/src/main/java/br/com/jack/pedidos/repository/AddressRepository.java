package br.com.jack.pedidos.repository;

import br.com.jack.pedidos.domain.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> { }
