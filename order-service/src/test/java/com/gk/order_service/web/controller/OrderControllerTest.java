package com.gk.order_service.web.controller;

import com.gk.order_service.AbstractIT;
import com.gk.order_service.testdata.TestDataFactory;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

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
}