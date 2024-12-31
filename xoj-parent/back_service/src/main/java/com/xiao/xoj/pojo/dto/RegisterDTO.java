package com.xiao.xoj.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/6/10 1:10
 * @description: TODO
 */
@Data
@Accessors(chain = true)
public class RegisterDTO implements Serializable {


    @NotBlank(message = "用户名不能为空")
    @Length(max = 20, message = "用户名不能超过20位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(max = 20, min = 6, message = "密码长度应该为6~20位！")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Length(max = 6, min = 6, message = "验证码错误")
    private String code;
}
