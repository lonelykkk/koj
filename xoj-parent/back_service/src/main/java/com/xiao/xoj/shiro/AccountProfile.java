package com.xiao.xoj.shiro;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/6/8 21:01
 * @description: 存在redis session的当前登录用户信息
 */
@Data
public class AccountProfile implements Serializable {

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "真实姓名")
    private String realname;

    @ApiModelProperty(value = "班级")
    private String classe;

    @ApiModelProperty(value = "学号")
    private String number;

    @ApiModelProperty(value = "头像地址")
    private String avatar;

    @ApiModelProperty(value = "是否禁用：1（true）已禁用，0（false）未禁用")
    private Boolean isDisabled;

    public String getId() { //shiro登录用户实体默认主键获取方法要为getId
        return uid;
    }
}
