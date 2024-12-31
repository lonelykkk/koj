package com.xiao.xoj.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/5/20 16:50
 * @description: TODO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TestJudgeRes implements Serializable {

    private static final long serialVersionUID = 888L;

    // 评测结果状态码
    private Integer status;

    // 评测运行时间消耗 ms
    private Long time;

    // 评测运行空间消耗 kb
    private Long memory;

    // 输入
    private String Input;

    // 期望输出
    private String expectedOutput;

    // 运行标准输出
    private String stdout;

    // 运行错误输出
    private String stderr;

    // 原题的评测模式：default、spj、interactive
    private String problemJudgeMode;
}
