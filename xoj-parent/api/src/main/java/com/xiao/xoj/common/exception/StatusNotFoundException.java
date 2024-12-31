package com.xiao.xoj.common.exception;

/**
 * @author 肖恩
 * @create 2023/3/21 15:17
 * @description: TODO
 */
public class StatusNotFoundException extends RuntimeException{

    public StatusNotFoundException() {
    }

    public StatusNotFoundException(String message) {
        super(message);
    }

    public StatusNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusNotFoundException(Throwable cause) {
        super(cause);
    }

    public StatusNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
