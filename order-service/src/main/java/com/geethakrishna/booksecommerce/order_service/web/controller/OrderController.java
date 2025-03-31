package com.geethakrishna.booksecommerce.order_service.web.controller;

import com.geethakrishna.booksecommerce.order_service.domain.*;
import com.geethakrishna.booksecommerce.order_service.domain.models.CreateOrderRequest;
import com.geethakrishna.booksecommerce.order_service.domain.models.CreateOrderResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final SecurityService securityService;

    OrderController(OrderService orderService, SecurityService securityService) {
        this.orderService = orderService;
        this.securityService = securityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String userName = securityService.getLoginUserName();
        log.info("Creating order for user: {}", userName);
        return orderService.createOrder(userName, request);
    }

    @GetMapping
    List<OrderSummary> getOrders() {
        String userName = securityService.getLoginUserName();
        log.info("Listing the orders of user: {}", userName);
        return orderService.getOrders(userName);
    }

    @GetMapping("/{orderNumber}")
    OrderDTO getOrderDetails(@PathVariable String orderNumber) {
        log.info("Fetching the order: {}", orderNumber);
        String userName = securityService.getLoginUserName();
        return orderService
                .getOrder(userName, orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found for order: " + orderNumber));
    }
}
