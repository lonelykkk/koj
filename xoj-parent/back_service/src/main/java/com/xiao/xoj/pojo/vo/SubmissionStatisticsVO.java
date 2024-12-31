package com.xiao.xoj.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/7/13 8:16
 * @description: TODO
 */
@Data
public class SubmissionStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "最近七天日期格式 mm-dd,升序")
    private List<String> dateStrList;

    @ApiModelProperty(value = "最近七天每天AC数量")
    private List<Long> acCountList;

    @ApiModelProperty(value = "最近七天每天提交数量")
    private List<Long> totalCountList;

}

