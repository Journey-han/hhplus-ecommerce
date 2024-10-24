package io.hhplus.ecommerce.app.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    PRODUCT_NOT_FOUND(404, "Product not found"),
    OUT_OF_STOCK(409, "Out of stock"),
    USER_NOT_FOUND(404, "User not found"),
    BALANCE_NOT_ENOUGH(402, "Not enough balance"),
    ORDER_CREATION_FAILED(500, "Order creation failed"),
    PAYMENT_FAILED(500, "Payment failed");

    private final int status;
    private final String message;
}
