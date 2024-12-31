package com.xiao.xoj.common.exception;

import lombok.Data;

/**
 * @author 肖恩
 * @create 2023/5/10 20:39
 * @description: TODO
 */
@Data
public class CompileError extends Exception{

    private String message;
    private String stdout;
    private String stderr;

    public CompileError(String message, String stdout, String stderr) {
        super(message);
        this.message = message;
        this.stdout = stdout;
        this.stderr = stderr;
    }

}
