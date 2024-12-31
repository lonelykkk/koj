package com.xiao.xoj.pojo.vo;

import com.xiao.xoj.pojo.entity.contest.ContestCorrection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/7/14 12:34
 * @description: TODO
 */
@Data
public class ContestStudentInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    private String id;

    @ApiModelProperty(value = "登录账号")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    private String realname;

    @ApiModelProperty(value = "学号")
    private String number;

    @ApiModelProperty(value = "班级")
    private String classe;

    @ApiModelProperty(value = "性别 1 女，2 男")
    private Integer sex;

    @ApiModelProperty(value = "是否已经批改")
    private Boolean isCorrection;

    @ApiModelProperty(value = "批改信息")
    private ContestCorrection contestCorrection;

}
