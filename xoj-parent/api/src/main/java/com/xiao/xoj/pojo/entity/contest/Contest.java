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
@ApiModel(value="Contest对象", description="")
public class Contest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自动递增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "比赛创建者id")
    private String userId;

    @ApiModelProperty(value = "比赛创建者的用户名")
    private String username;

    @ApiModelProperty(value = "比赛标题")
    private String title;

    @ApiModelProperty(value = "0为acm赛制，1为比分赛制（默认0）")
    private Integer type;

    @ApiModelProperty(value = "比赛说明")
    private String description;

    @ApiModelProperty(value = "0为公开赛，1为私有赛（访问有密码）")
    private Integer auth;

    @ApiModelProperty(value = "比赛密码")
    private String pwd;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "比赛时长(s)")
    private Integer duration;

    @ApiModelProperty(value = "-1为未开始，0为进行中，1为已结束")
    private Integer status;

    @ApiModelProperty(value = "是否开启账号限制（默认0）")
    private Boolean openAccountLimit;

    @ApiModelProperty(value = "账号限制规则")
    private String accountLimitRule;

    @ApiModelProperty(value = "逻辑删除：1（true）已删除，0（false）未删除")
    private Boolean isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;


}
