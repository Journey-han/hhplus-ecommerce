package io.hhplus.ecommerce.app.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StockTransactionType {
    RESTOCKED("RESTOCKED"),
    OUTBOUND("OUTBOUND"),
    RETURN("RETURN");

    private final String message;
}

