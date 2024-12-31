package com.xiao.xoj.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/8/30 21:06
 * @description: TODO
 */
@Data
@Accessors(chain = true)
public class ProblemCaseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "题目ID")
    private Integer pid;

    @ApiModelProperty(value = "修改文件名称")
    private String fileName;

    @ApiModelProperty(value = "修改内容")
    private String updateContent;

}
