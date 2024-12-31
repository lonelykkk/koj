package com.xiao.xoj.pojo.vo;

import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.entity.problem.Tag;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/4/18 20:55
 * @description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "题目内容")
    private Problem problem;

    @ApiModelProperty(value = "题目描述")
    private String description;

    @ApiModelProperty(value = "题目标签")
    private List<Tag> tags;

    @ApiModelProperty(value = "题目提交统计情况")
    private ProblemCountVO problemCount;

}
