package com.xiao.xoj.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/4/18 16:34
 * @description: 用户代码提交类
 */
@Data
@Accessors(chain = true)
public class SubmitJudgeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "题目id不能为空")
    private Integer pid;

    @NotBlank(message = "代码语言选择不能为空")
    private String language;

    @NotBlank(message = "提交的代码不能为空")
    private String code;

    @NotBlank(message = "提交的比赛id所属不能为空，若并非比赛提交，请设置为0")
    private Integer cid;

}
