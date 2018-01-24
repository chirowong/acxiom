package com.acxiom.interview.exception;

public class BaseApiException extends RuntimeException {

    private static final long serialVersionUID = 2570059268761602501L;

    public BaseApiException() {
        this("Api调用异常");
    }

    public BaseApiException(String message) {
        super(message);
    }

    public BaseApiException(Throwable cause) {
        super(cause);
    }

    public BaseApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
