package com.xiao.xoj.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/6/9 17:31
 * @description: TODO
 */
@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    private String id;

    @ApiModelProperty(value = "微信openid")
    private String openid;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "登录账号")
    @NotBlank
    @Length(max = 20, message = "用户名不能超过20位")
    private String username;

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
    @Email
    private String email;

    @ApiModelProperty(value = "角色列表")
    private List<String> roleList;

}
