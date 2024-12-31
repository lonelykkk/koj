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
@ApiModel(value="Reply对象", description="")
public class Reply implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自动递增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "评论id")
    private Integer commentId;

    @ApiModelProperty(value = "发起回复的用户id")
    private String fromUserId;

    @ApiModelProperty(value = "发起回复的用户名")
    private String fromUsername;

    @ApiModelProperty(value = "发起回复的用户头像")
    private String fromAvatar;

    @ApiModelProperty(value = "被回复的用户id")
    private String toUserId;

    @ApiModelProperty(value = "被回复的用户名")
    private String toUsername;

    @ApiModelProperty(value = "被回复的用户头像")
    private String toAvatar;

    @ApiModelProperty(value = "回复内容")
    private String content;

    @ApiModelProperty(value = "逻辑删除：1（true）已删除，0（false）未删除")
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;


}
