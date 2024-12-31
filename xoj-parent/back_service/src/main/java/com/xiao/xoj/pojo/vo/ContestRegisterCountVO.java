package com.xiao.xoj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/6/4 19:31
 * @description: TODO
 */
@Data
@ApiModel(value="比赛报名统计", description="")
public class ContestRegisterCountVO implements Serializable {

    @ApiModelProperty(value = "比赛id")
    private Integer cid;

    @ApiModelProperty(value = "比赛报名人数")
    private Integer count;

}
