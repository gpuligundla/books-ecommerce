package com.gk.order_service.web.controller;

import com.gk.order_service.AbstractIT;
import com.gk.order_service.domain.OrderSummary;
import com.gk.order_service.testdata.TestDataFactory;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import io.restassured.common.mapper.TypeRef;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.assertj.core.api.Assertions.assertThat;

@Sql("/test-orders.sql")
class OrderControllerTests extends AbstractIT {

    @Nested
    class CreateOrderTests {
        @Test
        void shouldCreateOrderSuccessfully() {
            mockGetProductByCode("P100", "Product 1", new BigDecimal(25.50));
            var payload =
                    """
                                    {
                                        "customer": {
                                            "name": "GK",
                                            "email": "gk@gmail.com",
                                            "phone": "9999999999"
                                        },
                                        "deliveryAddress": {
                                            "addressLine1": "220 ELM st",
                                            "addressLine2": "APT 204",
                                            "city": "Clemson",
                                            "state": "SC",
                                            "zipCode": "29631",
                                            "country": "USA"
                                        },
                                        "items": [
                                            {
                                                "code": "P100",
                                                "name": "Product 1",\s
                                                "price": 25.50,
                                                "quantity": 1
                                            }
                                        ]
                                    }
                            """;
            given().contentType(ContentType.JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("orderNumber", notNullValue());
        }

        @Test
        void shouldReturnBadRequestWhenMandatoryDataIsMissing() {
            var payload = TestDataFactory.createOrderRequestWithInvalidCustomer();
            given().contentType(ContentType.JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class GetOrdersTests {
        @Test
        void shouldGetOrdersSuccessfully() {
            List<OrderSummary> orderSummaries = given().when()
                    .get("/api/orders")
                    .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(new TypeRef<>(){});
            System.out.println(orderSummaries);
            assertThat(orderSummaries).hasSize(2);
        }
    }

    @Nested
    class GetOrderByOrderNumberTests {
        String orderNumber = "order-123";

        @Test
        void shouldGetOrderSuccessfully() {
            given().when()
                    .get("/api/orders/{orderNumber}", orderNumber)
                    .then()
                    .statusCode(200)
                    .body("orderNumber", is(orderNumber))
                    .body("items.size()", is(2));
        }
    }
}