package com.xiao.xoj.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/5/19 21:12
 * @description: 查询评测状态类
 */
@Data
public class SubmitIdListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "查询的提交id列表不能为空")
    private List<Integer> submitIds;

    private Integer cid;

}
