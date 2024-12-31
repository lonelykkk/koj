package com.xiao.xoj.pojo.entity.judge;

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
 * @since 2023-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="JudgeCase对象", description="")
public class JudgeCase implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自动递增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "提交id")
    private Integer submitId;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "题目id")
    private Integer pid;

    @ApiModelProperty(value = "评测样例id")
    private Integer caseId;

    @ApiModelProperty(value = "评测结果")
    private Integer status;

    @ApiModelProperty(value = "测试该测试样例所用时间（ms）")
    private Integer runTime;

    @ApiModelProperty(value = "测试该测试样例所用空间（kb）")
    private Integer runMemory;

    @ApiModelProperty(value = "排序")
    private Integer seq;

    @ApiModelProperty(value = "default，subtask_lowest，subtask_average")
    private String mode;

    @ApiModelProperty(value = "样例输入，输入文件名")
    private String inputData;

    @ApiModelProperty(value = "样例输出，输出文件名")
    private String output;

    @ApiModelProperty(value = "用户样例输出，用于记录对单个测试点的输出或信息提示")
    private String userOutput;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;


}
