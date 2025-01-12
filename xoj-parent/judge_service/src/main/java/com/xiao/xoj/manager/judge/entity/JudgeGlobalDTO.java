package com.xiao.xoj.manager.judge.entity;

import cn.hutool.json.JSONObject;
import com.xiao.xoj.utils.Constants;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/5/15 9:23
 * @description: 一次评测全局通用的传输实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class JudgeGlobalDTO implements Serializable {

    private static final long serialVersionUID = 888L;

    // 当前评测题目的id
    private Integer problemId;

    // 当前评测题目的模式
    private Constants.JudgeMode judgeMode;

    // 用户程序在沙盒编译后对应内存文件的id，运行时需要传入
    private String userFileId;

    // 用户程序代码文件的内容
    private String userFileContent;

    // 整个评测的工作目录
    private String runDir;

    // 判题沙盒评测程序的最大实际时间，一般为题目最大限制时间+200ms
    private Long testTime;

    // 当前题目评测的最大时间限制 ms
    private Long maxTime;

    // 当前题目评测的最大空间限制 mb
    private Long maxMemory;

    // 当前题目评测的最大栈空间限制 mb
    private Integer maxStack;

    // 评测数据json内容
    private JSONObject testCaseInfo;

    // 普通评测的命令配置
    Constants.RunConfig runConfig;

    // 是否需要生成用户程序输出的文件
    private Boolean needUserOutputFile;

    // 是否需要自动移除评测数据的行末空格
    private Boolean removeEOLBlank;

}
