package com.xiao.xoj.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/4/18 16:35
 * @description: TODO
 */
@Data
@Accessors(chain = true)
public class TestJudgeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "题目id")
    private Long pid;

    @NotBlank(message = "评测类型：public、contest")
    private String type;

    @NotBlank(message = "代码")
    private String code;

    @NotBlank(message = "编程语言")
    private String language;

    @NotBlank(message = "输入")
    private String userInput;

    // 预期输出
    private String expectedOutput;

    // text/x-csrc 用于鉴别语言
    private String mode;

}