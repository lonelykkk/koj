package com.xiao.xoj.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/6/17 13:16
 * @description: 更改邮箱的结果类
 */
@Data
public class ChangeCodeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "修改邮箱邮件有效时间，单位秒")
    private Integer expire;
}
