package com.geethakrishna.booksecommerce.catalog_service.domain;

public class ProductNotFoundException extends RuntimeException {
    ProductNotFoundException(String message) {
        super(message);
    }

    public static ProductNotFoundException forCode(String code) {
        return new ProductNotFoundException("Prodcut not found for code: " + code);
    }
}
