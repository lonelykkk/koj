package com.xiao.xoj.common.exception;

import lombok.Data;

/**
 * @author 肖恩
 * @create 2023/5/10 20:40
 * @description: TODO
 */
@Data
public class RuntimeError extends Exception {
    private String message;
    private String stdout;
    private String stderr;

    public RuntimeError(String message, String stdout, String stderr) {
        super(message);
        this.message = message;
        this.stdout = stdout;
        this.stderr = stderr;
    }
}
