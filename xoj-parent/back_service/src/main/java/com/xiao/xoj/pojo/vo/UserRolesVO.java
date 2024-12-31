package com.xiao.xoj.pojo.vo;

import com.xiao.xoj.pojo.entity.user.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/6/9 19:25
 * @description: 用户信息以及其对应的角色
 */
@Data
public class UserRolesVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    private String id;

    @ApiModelProperty(value = "微信openid")
    private String openid;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "登录账号")
    private String username;

    @ApiModelProperty(value = "登录密码")
    private String password;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "用户签名")
    private String sign;

    @ApiModelProperty(value = "真实姓名")
    private String realname;

    @ApiModelProperty(value = "学号")
    private String number;

    @ApiModelProperty(value = "班级")
    private String classe;

    @ApiModelProperty(value = "性别 1 女，2 男")
    private Integer sex;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "是否禁用：1（true）已禁用，0（false）未禁用")
    private Boolean isDisabled;

    @ApiModelProperty(value = "逻辑删除：1（true）已删除，0（false）未删除")
    private Boolean isDeleted;

    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    private Date gmtModified;

    @ApiModelProperty(value = "角色列表")
    private List<Role> roles;
}