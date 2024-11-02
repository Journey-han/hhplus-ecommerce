package io.hhplus.ecommerce.app.domain.common;

import lombok.Data;

@Data
public class CommonApiResponse<T> {

    private int status;
    private String result;
    private String message;
    private T data;

    public CommonApiResponse(int status, String result, String message, T data) {
        this.status = status;
        this.result = result;
        this.message = message;
        this.data = data;
    }

}
