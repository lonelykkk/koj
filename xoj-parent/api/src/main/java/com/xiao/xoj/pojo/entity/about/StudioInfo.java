package com.xiao.xoj.pojo.entity.about;

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
 * @since 2023-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StudioInfo对象", description="")
public class StudioInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自动递增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "成员名称")
    private String name;

    @ApiModelProperty(value = "第几届（什么时候进入学校）")
    private Integer whichSession;

    @ApiModelProperty(value = "班级")
    private String classes;

    @ApiModelProperty(value = "qq")
    private String qq;

    @ApiModelProperty(value = "考研院校")
    private String school;

    @ApiModelProperty(value = "就业公司")
    private String company;

    @ApiModelProperty(value = "1：老师，0（默认）：成员")
    private Integer isTeacher;

    @ApiModelProperty(value = "逻辑删除：1（true）已删除，0（false）未删除")
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;


}
