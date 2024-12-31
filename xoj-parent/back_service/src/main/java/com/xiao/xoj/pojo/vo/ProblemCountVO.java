package com.xiao.xoj.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/4/18 20:56
 * @description: TODO
 */
@Data
@Accessors(chain = true)
public class ProblemCountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "题目Id")
    private Integer pid;

    @ApiModelProperty(value = "题目总提交数")
    private Integer total;

    @ApiModelProperty(value = "题目总通过数")
    private Integer ac;

    @ApiModelProperty(value = "空间超限")
    private Integer mle;

    @ApiModelProperty(value = "时间超限")
    private Integer tle;

    @ApiModelProperty(value = "运行错误")
    private Integer re;

    @ApiModelProperty(value = "格式错误")
    private Integer pe;

    @ApiModelProperty(value = "编译错误")
    private Integer ce;

    @ApiModelProperty(value = "答案错误")
    private Integer wa;

    @ApiModelProperty(value = "系统错误")
    private Integer se;

    @ApiModelProperty(value = "部分通过，OI题目")
    private Integer pa;

}
