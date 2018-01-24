package com.acxiom.interview.exception.user;

import com.acxiom.interview.exception.BaseApiException;

/**
 * @author chirowong
 */
public class UserIndexException extends BaseApiException {

    private static final long serialVersionUID = -3780147325440583830L;

    public UserIndexException() {
        this("用户搜索操作异常");
    }

    public UserIndexException(String message) {
        super(message);
    }

    public UserIndexException(Throwable cause) {
        super(cause);
    }
}
