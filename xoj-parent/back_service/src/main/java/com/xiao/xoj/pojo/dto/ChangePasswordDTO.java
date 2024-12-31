package com.xiao.xoj.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/6/17 13:25
 * @description: TODO
 */
@Data
public class ChangePasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String oldPassword;

    private String newPassword;
}
