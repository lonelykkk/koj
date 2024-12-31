package com.xiao.xoj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/8/28 20:06
 * @description: 系统信息封装类
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "系统信息封装类")
public class SystemInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "系统名称")
    private String systemName;

    @ApiModelProperty(value = "系统版本")
    private String systemVersion;

    @ApiModelProperty(value = "系统核心数")
    private Integer systemProcessors;

    @ApiModelProperty(value = "cup使用率")
    private Double cpuUsage;

    @ApiModelProperty(value = "总内存")
    private Long totalMemory;

    @ApiModelProperty(value = "已使用内存")
    private Long useMemory;

    @ApiModelProperty(value = "空闲内存")
    private Long freeMemory;

    @ApiModelProperty(value = "内存使用率")
    private Double memoryUsage;


}
