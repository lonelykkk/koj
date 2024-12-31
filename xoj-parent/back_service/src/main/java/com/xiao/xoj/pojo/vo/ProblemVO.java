package com.xiao.xoj.pojo.vo;

import com.xiao.xoj.pojo.entity.problem.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/4/18 20:36
 * @description: TODO
 */
@ApiModel(value = "题目列表查询对象ProblemVo")
@Data
public class ProblemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "题目id")
    private Integer pid;

    @ApiModelProperty(value = "题目展示id")
    private String problemId;

    @ApiModelProperty(value = "题目标题")
    private String title;

    @ApiModelProperty(value = "题目难度")
    private Integer difficulty;

    @ApiModelProperty(value = "题目类型")
    private Integer type;

    @ApiModelProperty(value = "题目标签")
    private List<Tag> tags;

    @ApiModelProperty(value = "该题总提交数")
    private Integer total;

    @ApiModelProperty(value = "该题ac数")
    private Integer ac;

    @ApiModelProperty(value = "用户是否AC，0：未ac，1：ac")
    private Boolean isAc;

}
