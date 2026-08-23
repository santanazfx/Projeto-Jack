package br.com.jack.pedidos.repository;

import br.com.jack.pedidos.domain.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> { }
