package com.gk.order_service.domain;

import com.gk.order_service.domain.models.OrderStatus;

public record OrderSummary(String orderNumber, OrderStatus status) {
}
