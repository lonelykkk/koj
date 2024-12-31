package com.xiao.xoj.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 肖恩
 * @create 2023/3/21 9:56
 * @description: 访问结果状态
 */
@Getter
@AllArgsConstructor
public enum  ResultStatus {

    SUCCESS(20000, "成功"),

    FAIL(40000, "失败"),

    ACCESS_DENIED(40001,"访问受限"),

    FORBIDDEN(40003,"拒绝访问"),

    NOT_FOUND(40004,"数据不存在"),

    SYSTEM_ERROR(50000,"系统错误");

    private Integer status;

    private String description;

}
