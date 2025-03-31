package com.geethakrishna.booksecommerce.order_service.web.controller;

import static com.geethakrishna.booksecommerce.order_service.testdata.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geethakrishna.booksecommerce.order_service.domain.OrderDTO;
import com.geethakrishna.booksecommerce.order_service.domain.OrderService;
import com.geethakrishna.booksecommerce.order_service.domain.OrderSummary;
import com.geethakrishna.booksecommerce.order_service.domain.SecurityService;
import com.geethakrishna.booksecommerce.order_service.domain.models.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(OrderController.class)
class OrderControllerUnitTests {
    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private SecurityService securityService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        given(securityService.getLoginUserName()).willReturn("Gk");
    }

    @ParameterizedTest(name = "[{index}]-{0}")
    @MethodSource("createOrderRequestProvider")
    void shouldReturnBadRequestWhenOrderPayloadIsInvalid(CreateOrderRequest request) throws Exception {
        given(orderService.createOrder(eq("Gk"), any(CreateOrderRequest.class))).willReturn(null);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> createOrderRequestProvider() {
        return Stream.of(
                arguments(named("Order with Invalid Customer", createOrderRequestWithInvalidCustomer())),
                arguments(named("Order with Invalid Delivery Address", createOrderRequestWithInvalidDeliveryAddress())),
                arguments(named("Order with No Items", createOrderRequestWithNoItems())));
    }

    @Test
    void shouldReturnOrdersSuccessfully() throws Exception {
        List<OrderSummary> mockOrders =
                List.of(new OrderSummary("A101", OrderStatus.NEW), new OrderSummary("A123", OrderStatus.CANCELLED));

        given(orderService.getOrders("Gk")).willReturn(mockOrders);

        MvcResult mvcResult = mockMvc.perform(get("/api/orders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<OrderSummary> actualOrders = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(actualOrders);
        assertEquals(2, actualOrders.size());
        assertEquals("A101", actualOrders.get(0).orderNumber());
        assertEquals("A123", actualOrders.get(1).orderNumber());
    }

    @Test
    void shouldReturnOrderDetailsWhenOrderExists() throws Exception {
        String orderNumber = "ORD123";
        OrderDTO mockOrder = new OrderDTO(
                orderNumber,
                "Gk",
                Set.of(new OrderItem("A101", "Product A", new BigDecimal(12.22), 2)),
                new Customer("gk", "gk@gmail.com", "86666235812"),
                new Address("220 ELM st", "University place", "Clemson", "SC", "29631", "US"),
                OrderStatus.NEW,
                "Order created",
                LocalDateTime.of(12, 12, 12, 12, 12));

        given(orderService.getOrder("Gk", orderNumber)).willReturn(Optional.of(mockOrder));

        MvcResult mvcResult = mockMvc.perform(
                        get("/api/orders/{orderNumber}", orderNumber).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        OrderDTO actualOrder = objectMapper.readValue(jsonResponse, OrderDTO.class);

        assertNotNull(actualOrder);
        assertEquals(orderNumber, actualOrder.orderNumber());
        assertEquals("Gk", actualOrder.user());
        assertEquals(1, actualOrder.items().size()); // Single item in set
        assertEquals("A101", actualOrder.items().iterator().next().code());
        assertEquals(2, actualOrder.items().iterator().next().quantity());
        assertEquals(
                new BigDecimal(12.22), actualOrder.items().iterator().next().price());
    }
}
