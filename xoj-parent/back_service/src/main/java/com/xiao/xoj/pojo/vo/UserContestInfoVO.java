package com.xiao.xoj.pojo.vo;

import com.xiao.xoj.pojo.entity.contest.ContestCorrection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 肖恩
 * @create 2023/7/14 21:21
 * @description: TODO
 */
@Data
public class UserContestInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @ApiModelProperty(value = "创建者用户名")
    private String username;

    @ApiModelProperty(value = "比赛标题")
    private String title;

    @ApiModelProperty(value = "0为acm赛制，1为比分赛制")
    private Integer type;

    @ApiModelProperty(value = "比赛说明")
    private String description;

    @ApiModelProperty(value = "-1为未开始，0为进行中，1为已结束")
    private Integer status;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "是否已经批改")
    private Boolean isCorrection;

    @ApiModelProperty(value = "批改信息")
    private ContestCorrection contestCorrection;

}
