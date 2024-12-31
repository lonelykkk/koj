package com.xiao.xoj.pojo.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/6/9 17:26
 * @description: TODO
 */
@Data
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空！")
    @Length(max = 20, message = "用户名不能超过20位")
    private String username;

    @NotBlank(message = "密码不能为空！")
    @Length(max = 20, min = 6, message = "密码长度应该为6~20位！")
    private String password;

}
