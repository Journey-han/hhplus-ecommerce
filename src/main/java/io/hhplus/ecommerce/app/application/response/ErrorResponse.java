package io.hhplus.ecommerce.app.application.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponse {

    private int status;
    private String message;
}
