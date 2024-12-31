package com.xiao.xoj.pojo.dto;

import com.xiao.xoj.pojo.entity.judge.Judge;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 肖恩
 * @create 2023/4/27 20:03
 * @description: TODO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ToJudgeDTO implements Serializable {

    private static final long serialVersionUID = 999L;

    // 判题数据实体类
    private Judge judge;

    // 调用评测验证的token
    private String token;

    // 调用判题机的ip
    private String judgeServerIp;

    // 调用判题机的port
    private Integer judgeServerPort;

}
