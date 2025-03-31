package com.geethakrishna.booksecommerce.order_service.domain;

import com.geethakrishna.booksecommerce.order_service.domain.models.OrderStatus;

public record OrderSummary(String orderNumber, OrderStatus status) {}
