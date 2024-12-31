package com.xiao.xoj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/6/4 19:07
 * @description: TODO
 */
@ApiModel(value = "比赛题目列表格式数据ContestProblemVO", description = "")
@Data
public class ContestProblemVO implements Serializable {

    private Integer id;

    @ApiModelProperty(value = "该题目在比赛中的顺序id")
    private String displayId;

    @ApiModelProperty(value = "比赛id")
    private Integer cid;

    @ApiModelProperty(value = "题目id")
    private Integer pid;

    @ApiModelProperty(value = "该题目在比赛中的标题，默认为原名字")
    private String displayTitle;

    @ApiModelProperty(value = "用户是否ac")
    private Boolean isAc;

    @ApiModelProperty(value = "用户该题的分数")
    private Integer score;

    @ApiModelProperty(value = "该题目的ac通过数")
    private Integer ac;

    @ApiModelProperty(value = "该题目的总提交数")
    private Integer total;

}
