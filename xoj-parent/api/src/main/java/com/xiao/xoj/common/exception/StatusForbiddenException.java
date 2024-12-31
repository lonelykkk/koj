package com.xiao.xoj.common.exception;

/**
 * @author 肖恩
 * @create 2023/3/21 15:18
 * @description: TODO
 */
public class StatusForbiddenException extends RuntimeException{

    public StatusForbiddenException() {
    }

    public StatusForbiddenException(String message) {
        super(message);
    }

    public StatusForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusForbiddenException(Throwable cause) {
        super(cause);
    }

    public StatusForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
