package com.xiao.xoj.common.exception;

/**
 * @author 肖恩
 * @create 2023/3/21 15:15
 * @description: TODO
 */
public class StatusFailException extends RuntimeException {

    public StatusFailException() {
    }

    public StatusFailException(String message) {
        super(message);
    }

    public StatusFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusFailException(Throwable cause) {
        super(cause);
    }

    public StatusFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
