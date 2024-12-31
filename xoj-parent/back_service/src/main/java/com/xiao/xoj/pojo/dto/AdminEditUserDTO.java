package com.xiao.xoj.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/6/9 23:58
 * @description: TODO
 */
@Data
public class AdminEditUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "username不能为空")
    private String username;

    @NotBlank(message = "id不能为空")
    private String id;

    private String realname;

    private String email;

    private String password;

    private Integer type;

    private String number;

    private String classe;

    private Integer sex;

    private Boolean isDisabled;

    private Boolean setNewPwd;
}
