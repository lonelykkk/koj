package com.xiao.xoj.common.exception;

/**
 * @author 肖恩
 * @create 2023/3/21 15:17
 * @description: TODO
 */
public class StatusSystemErrorException extends RuntimeException {

    public StatusSystemErrorException() {
    }

    public StatusSystemErrorException(String message) {
        super(message);
    }

    public StatusSystemErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusSystemErrorException(Throwable cause) {
        super(cause);
    }

    public StatusSystemErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
