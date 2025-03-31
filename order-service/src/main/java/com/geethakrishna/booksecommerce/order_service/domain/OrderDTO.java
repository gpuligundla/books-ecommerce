package com.geethakrishna.booksecommerce.order_service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.geethakrishna.booksecommerce.order_service.domain.models.Address;
import com.geethakrishna.booksecommerce.order_service.domain.models.Customer;
import com.geethakrishna.booksecommerce.order_service.domain.models.OrderItem;
import com.geethakrishna.booksecommerce.order_service.domain.models.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record OrderDTO(
        String orderNumber,
        String user,
        Set<OrderItem> items,
        Customer customer,
        Address deliveryAddress,
        OrderStatus status,
        String comments,
        LocalDateTime createdAt) {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
