package com.geethakrishna.booksecommerce.order_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEventRepository extends JpaRepository<OrderEventEntity, Long> {}
