package com.xiao.xoj.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 肖恩
 * @create 2023/5/24 17:56
 * @description: TODO
 */
@Data
@ApiModel(value = "讨论数据VO", description = "")
public class DiscussionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @ApiModelProperty(value = "分类id")
    private Integer categoryId;

    @ApiModelProperty(value = "分类名字")
    private String categoryName;

    @ApiModelProperty(value = "讨论标题")
    private String title;

    @ApiModelProperty(value = "讨论简介")
    private String description;

    @ApiModelProperty(value = "讨论内容")
    private String content;

    @ApiModelProperty(value = "题目关联 默认为null则不关联题目")
    private Integer pid;

    @ApiModelProperty(value = "发表者id")
    private String uid;

    @ApiModelProperty(value = "发表者用户名")
    private String author;

    @ApiModelProperty(value = "发表者头像地址")
    private String avatar;

    @ApiModelProperty(value = "浏览数量")
    private Integer viewNum;

    @ApiModelProperty(value = "点赞数量")
    private Integer likeNum;

    @ApiModelProperty(value = "如果有登录的话，是否点赞了")
    private Boolean hasLike;

    @ApiModelProperty(value = "优先级，是否置顶")
    private Boolean topPriority;

    @ApiModelProperty(value = "是否封禁 0正常，1封禁")
    private Boolean isDisabled;

    private Date gmtCreate;

    private Date gmtModified;
}
