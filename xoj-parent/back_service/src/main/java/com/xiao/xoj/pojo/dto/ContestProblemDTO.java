package com.xiao.xoj.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @author 肖恩
 * @create 2023/5/31 20:45
 * @description: TODO
 */
@Data
public class ContestProblemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "题目id不能为空")
    private Integer pid;

    @NotNull(message = "比赛id不能为空")
    private Integer cid;

    @NotBlank(message = "题目在比赛中的展示id不能为空")
    private String displayId;

    // 题目展示标题，默认为原题标题
    private String displayTitle;

}
