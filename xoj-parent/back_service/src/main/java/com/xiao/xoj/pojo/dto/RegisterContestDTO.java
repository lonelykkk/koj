package com.xiao.xoj.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/6/4 19:02
 * @description: TODO
 */
@Data
public class RegisterContestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "cid不能为空")
    private Integer cid;

    private String password;

}
