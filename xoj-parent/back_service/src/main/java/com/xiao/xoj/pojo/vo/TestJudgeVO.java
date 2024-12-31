package com.xiao.xoj.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/5/20 16:40
 * @description: 测试用例评测结果返回结果类
 */
@Data
public class TestJudgeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 评测状态码
    private Integer status;

    // 运行时间 ms
    private Long time;

    // 运行空间 kb
    private Long memory;

    // 用户输入
    private String userInput;

    // 用户输出
    private String userOutput;

    // 预期输出
    private String expectedOutput;

    // 错误信息
    private String stderr;

    // 原题的评测模式：default、spj、interactive
    private String problemJudgeMode;

}
