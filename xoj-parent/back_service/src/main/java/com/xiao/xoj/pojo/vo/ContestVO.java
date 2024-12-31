package com.xiao.xoj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 肖恩
 * @create 2023/6/4 18:53
 * @description: TODO
 */
@ApiModel(value="考核信息", description="")
@Data
public class ContestVO implements Serializable {

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

    @ApiModelProperty(value = "0为公开赛，1为私有赛（有密码）")
    private Integer auth;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "比赛时长（秒）")
    private Integer duration;

    @ApiModelProperty("当前服务器系统时间，为了前端统一时间")
    private Date now;

    @ApiModelProperty(value = "比赛的报名人数")
    private Integer count;


}
