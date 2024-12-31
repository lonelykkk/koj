package com.xiao.xoj.pojo.vo;

import com.xiao.xoj.pojo.entity.problem.ProblemCase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/8/30 20:51
 * @description: TODO
 */
@Data
@Accessors(chain = true)
public class ProblemCaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ProblemCase problemCase;

    private String input;

    private String output;

}
