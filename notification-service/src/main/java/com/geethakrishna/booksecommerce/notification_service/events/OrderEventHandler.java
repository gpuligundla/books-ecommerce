package com.geethakrishna.booksecommerce.notification_service.events;

import com.geethakrishna.booksecommerce.notification_service.domain.NotificationService;
import com.geethakrishna.booksecommerce.notification_service.domain.OrderEventEntity;
import com.geethakrishna.booksecommerce.notification_service.domain.OrderEventRepository;
import com.geethakrishna.booksecommerce.notification_service.domain.models.OrderCancelledEvent;
import com.geethakrishna.booksecommerce.notification_service.domain.models.OrderCreatedEvent;
import com.geethakrishna.booksecommerce.notification_service.domain.models.OrderDeliveredEvent;
import com.geethakrishna.booksecommerce.notification_service.domain.models.OrderErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderEventHandler.class);

    private final NotificationService notificationService;
    private final OrderEventRepository orderEventRepository;

    public OrderEventHandler(NotificationService notificationService, OrderEventRepository orderEventRepository) {
        this.notificationService = notificationService;
        this.orderEventRepository = orderEventRepository;
    }

    @RabbitListener(queues = "${notifications.new-orders-queue}")
    void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Order created event: {}", event);
        if (orderEventRepository.existsByEventId(event.eventId())) {
            log.warn("Received a duplicate created event: {}", event);
            return;
        }
        notificationService.sendOrderCreatedNotification(event);
        orderEventRepository.save(new OrderEventEntity(event.eventId()));
    }

    @RabbitListener(queues = "${notifications.cancelled-orders-queue}")
    void handleOrderCancelledEvent(OrderCancelledEvent event) {
        log.info("Order cancelled event: {}", event);
        if (orderEventRepository.existsByEventId(event.eventId())) {
            log.warn("Received a duplicate cancelled event: {}", event);
            return;
        }
        notificationService.sendOrderCancelledNotification(event);
        orderEventRepository.save(new OrderEventEntity(event.eventId()));
    }

    @RabbitListener(queues = "${notifications.delivered-orders-queue}")
    void handleOrderDeliveredEvent(OrderDeliveredEvent event) {
        log.info("Order delivered event: {}", event);
        if (orderEventRepository.existsByEventId(event.eventId())) {
            log.warn("Received a duplicate delivered event: {}", event);
            return;
        }
        notificationService.sendOrderDeliveredNotification(event);
        orderEventRepository.save(new OrderEventEntity(event.eventId()));
    }

    @RabbitListener(queues = "${notifications.error-orders-queue}")
    void handleOrderErrorEvent(OrderErrorEvent event) {
        log.info("Order error event: {}", event);
        if (orderEventRepository.existsByEventId(event.eventId())) {
            log.warn("Received a duplicate error event: {}", event);
            return;
        }
        notificationService.sendOrderErrorEventNotification(event);
        orderEventRepository.save(new OrderEventEntity(event.eventId()));
    }
}
