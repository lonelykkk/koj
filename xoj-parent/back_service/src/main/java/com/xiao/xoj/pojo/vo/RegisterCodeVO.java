package com.xiao.xoj.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/6/10 1:08
 * @description: TODO
 */
@Data
public class RegisterCodeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "注册邮件有效时间，单位秒")
    private Integer expire;
}
