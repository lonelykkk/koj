package com.xiao.xoj.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/6/10 1:17
 * @description: TODO
 */
@Data
public class CheckUsernameOrEmailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email;

    private String username;
}
