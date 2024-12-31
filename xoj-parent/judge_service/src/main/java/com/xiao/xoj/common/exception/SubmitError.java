package com.xiao.xoj.common.exception;

import lombok.Data;

/**
 * @author 肖恩
 * @create 2023/5/10 20:41
 * @description: TODO
 */
@Data
public class SubmitError extends Exception {
    private String message;
    private String stdout;
    private String stderr;

    public SubmitError(String message, String stdout, String stderr) {
        super(message);
        this.message = message;
        this.stdout = stdout;
        this.stderr = stderr;
    }
}
