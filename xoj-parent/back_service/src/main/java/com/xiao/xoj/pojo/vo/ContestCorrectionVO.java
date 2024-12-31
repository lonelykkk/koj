package com.xiao.xoj.pojo.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xiao.xoj.pojo.entity.contest.ContestCorrection;
import com.xiao.xoj.pojo.entity.judge.Judge;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/7/16 9:29
 * @description: TODO
 */
@Data
@Accessors(chain = true)
@ApiModel(value="ContestCorrection对象", description="")
public class ContestCorrectionVO implements Serializable {

    @ApiModelProperty(value = "批改内容")
    private ContestCorrection contestCorrection;

    @ApiModelProperty(value = "考核代码")
    private List<Judge> codes;

    @ApiModelProperty(value = "当前状态，是否有人修改")
    private Boolean canMod;

    @ApiModelProperty(value = "修改人")
    private String username;
}