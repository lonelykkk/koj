package com.xiao.xoj.pojo.entity.discussion;

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
 * @since 2023-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Discussion对象", description="")
public class Discussion implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自动递增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "分类id")
    private Integer categoryId;

    @ApiModelProperty(value = "贴子标题")
    private String title;

    @ApiModelProperty(value = "讨论简介")
    private String description;

    @ApiModelProperty(value = "讨论内容")
    private String content;

    @ApiModelProperty(value = "是否关联题目：0，未关联，非0(pid)，关联")
    private Integer problemId;

    @ApiModelProperty(value = "发帖者id")
    private String userId;

    @ApiModelProperty(value = "发贴者用户名")
    private String author;

    @ApiModelProperty(value = "发帖者头像")
    private String avatar;

    @ApiModelProperty(value = "浏览数")
    private Integer viewNum;

    @ApiModelProperty(value = "点赞数")
    private Integer likeNum;

    @ApiModelProperty(value = "评论数")
    private Integer commentNum;

    @ApiModelProperty(value = "优先级，是否置顶：0，不置顶，1，置顶")
    private Boolean topPriority;

    @ApiModelProperty(value = "是否封禁：0，未封禁，1，封禁")
    private Boolean isDisabled;

    @ApiModelProperty(value = "逻辑删除：1（true）已删除，0（false）未删除")
    private Boolean isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;


}
