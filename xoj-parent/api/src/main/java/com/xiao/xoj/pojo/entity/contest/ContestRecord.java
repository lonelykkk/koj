package com.xiao.xoj.pojo.entity.contest;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ContestRecord对象", description="")
public class ContestRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自动递增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "比赛id")
    private Integer cid;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "题目id")
    private Integer pid;

    @ApiModelProperty(value = "比赛中题目的id")
    private Integer cpid;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    private String realname;

    @ApiModelProperty(value = "比赛中展示的id")
    private String displayId;

    @ApiModelProperty(value = "提交id")
    private Integer submitId;

    @ApiModelProperty(value = "提交结果，0表示未AC通过不罚时，1表示AC通过，-1为未AC通过算罚时")
    private Integer status;

    @ApiModelProperty(value = "具体提交时间")
    private Date submitTime;

    @ApiModelProperty(value = "提交时间，为提交时间减去比赛时间")
    private Integer time;

    @ApiModelProperty(value = "OI比赛的得分")
    private Integer score;

    @ApiModelProperty(value = "运行耗时")
    private Integer useTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;


}
