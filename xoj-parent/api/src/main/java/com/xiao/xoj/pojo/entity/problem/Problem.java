package com.xiao.xoj.pojo.entity.problem;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Problem对象", description="")
public class Problem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自动递增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "自定义题目ID，如XOJ-1000")
    private String problemId;

    @ApiModelProperty(value = "题目标题")
    private String title;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "题目类型：0为ACM，1为OI")
    private Integer type;

    @ApiModelProperty(value = "时间限制，单位ms，默认为c/c++限制,其它语言为2倍")
    private Integer timeLimit;

    @ApiModelProperty(value = "内存限制，单位kb，默认为c/c++限制,其它语言为2倍")
    private Integer memoryLimit;

    @ApiModelProperty(value = "栈限制，单位mb，默认为128")
    private Integer stackLimit;

    @ApiModelProperty(value = "输入描述")
    private String input;

    @ApiModelProperty(value = "输出描述")
    private String output;

    @ApiModelProperty(value = "题目样例，不纳入评测数据")
    private String examples;

    @ApiModelProperty(value = "题目来源，（比赛id），默认为hoj,可能为爬虫vj")
    private String source;

    @ApiModelProperty(value = "题目难度：0简单，1中等，2困难")
    private Integer difficulty;

    @ApiModelProperty(value = "备注，提醒")
    private String hint;

    @ApiModelProperty(value = "默认为1，1公开，2为私有，3为考核题目")
    private Integer auth;

    @ApiModelProperty(value = "当该题目为IO题目时的分数，默认为100")
    private Integer ioScore;

    @ApiModelProperty(value = "是否去除用户代码的文末空格：0,去除，1，不去除")
    private Boolean isRemoveEndBlank;

    @ApiModelProperty(value = "是否开启题目测试样例的查看：0,开启，1，不开启")
    private Boolean openCaseResult;

    @ApiModelProperty(value = "题目测试数据是否是上传文件的：0，是上传的，1，不是上传的")
    private Boolean isUploadCase;

    @ApiModelProperty(value = "题目测试数据的版本号")
    private String caseVersion;

    @ApiModelProperty(value = "修改题目的管理员用户名，username")
    private String modifiedUser;

    @ApiModelProperty(value = "逻辑删除：1（true）已删除，0（false）未删除")
    private Boolean isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;


}
