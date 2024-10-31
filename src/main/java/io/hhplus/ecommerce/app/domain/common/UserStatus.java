package io.hhplus.ecommerce.app.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

    ACTIVATE("ACTIVATE"),
    DEACTIVATE("DEACTIVATE"),
    WITHDRAWAL("WITHDRAWAL");

    private final String message;
}
