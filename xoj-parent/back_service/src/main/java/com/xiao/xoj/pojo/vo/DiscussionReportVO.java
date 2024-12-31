package com.xiao.xoj.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 肖恩
 * @create 2023/7/11 13:39
 * @description: TODO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DiscussionReportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @ApiModelProperty(value = "讨论id")
    private Integer discussionId;

    @ApiModelProperty(value = "讨论标题")
    private String discussionTitle;

    @ApiModelProperty(value = "讨论作者")
    private String discussionAuthor;

    @ApiModelProperty(value = "举报者的用户名")
    private String reporter;

    @ApiModelProperty(value = "举报内容")
    private String content;

    @ApiModelProperty(value = "是否已读")
    private Boolean status;

    private Date gmtCreate;

    private Date gmtModified;

}
