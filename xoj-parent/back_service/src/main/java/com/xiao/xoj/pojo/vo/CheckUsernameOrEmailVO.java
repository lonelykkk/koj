package com.xiao.xoj.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/6/10 1:18
 * @description: TODO
 */
@Data
public class CheckUsernameOrEmailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean email;

    private Boolean username;
}