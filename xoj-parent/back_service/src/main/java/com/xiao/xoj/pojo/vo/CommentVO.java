package com.xiao.xoj.pojo.vo;

import com.xiao.xoj.pojo.entity.discussion.Reply;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/5/25 9:21
 * @description: 评论数据列表VO
 */
@ApiModel(value = "评论数据列表VO", description = "")
@Data
public class CommentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "评论id")
    private Integer id;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "评论者id")
    private String fromUserId;

    @ApiModelProperty(value = "评论者用户名")
    private String fromUsername;

    @ApiModelProperty(value = "评论组头像地址")
    private String fromAvatar;

    @ApiModelProperty(value = "点赞数量")
    private Integer likeNum;

    @ApiModelProperty(value = "该评论的总回复数")
    private Integer totalReplyNum;

    private Date gmtCreate;

    @ApiModelProperty(value = "该评论回复列表")
    private List<Reply> replyList;
}
