package com.acxiom.interview.exception;

import org.springframework.http.HttpStatus;

/**
 * 统一搜索服务异常
 * @author wangzhiliang
 */
public class SearchException extends RuntimeException {

    private HttpStatus httpStatus;

    private String errorCode;

    public SearchException(HttpStatus httpStatus, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public SearchException(String errorCode, String message, Throwable cause) {
        // 默认500状态码
        this(HttpStatus.INTERNAL_SERVER_ERROR, errorCode, message, cause);
    }

    public SearchException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    public SearchException() {
        this("搜索服务调用异常");
    }

    public SearchException(String message) {
        super(message);
    }

    public SearchException(Throwable cause) {
        this("搜索服务调用异常", cause);
    }

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
