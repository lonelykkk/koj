package com.xiao.xoj.pojo.entity.judge;

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
 * @since 2023-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Judge对象", description="")
public class Judge implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自动递增")
    @TableId(value = "submit_id", type = IdType.AUTO)
    private Integer submitId;

    @ApiModelProperty(value = "题目id")
    private Integer pid;

    @ApiModelProperty(value = "题目展示ID")
    private String displayId;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "提交时间")
    private Date submitTime;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "错误提醒")
    private String errorMessage;

    @ApiModelProperty(value = "运行时间（ms）")
    private Integer runTime;

    @ApiModelProperty(value = "运行内存（kb）")
    private Integer runMemory;

    @ApiModelProperty(value = "代码")
    private String code;

    @ApiModelProperty(value = "代码长度")
    private Integer codeLength;

    @ApiModelProperty(value = "代码语言")
    private String language;

    @ApiModelProperty(value = "分数")
    private Integer score;

    @ApiModelProperty(value = "比赛id，非比赛题目默认为0")
    private Integer cid;

    @ApiModelProperty(value = "比赛中题目排序id，非比赛题目默认为0")
    private Integer cpid;

    @ApiModelProperty(value = "判题机Ip")
    private String judger;

    @ApiModelProperty(value = "提交者所在ip")
    private String ip;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;


}
