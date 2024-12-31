package com.xiao.xoj.pojo.dto;

import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.entity.problem.Tag;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/3/22 17:11
 * @description: TODO
 */
@Data
@Accessors(chain = true)
public class ProblemDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Problem problem;

    private String description; // 题目描述

    private List<String> inputSamples; // 输入样例数据

    private List<String> outputSamples; // 输出样例数据

    private Boolean isUploadZipTestCase; // 测试数据是否是上传的

    private Boolean isUpdateTestcase; // 测试数据是否需要更新

    private String uploadFileDir; // 文件夹地址，这个文件夹中包含zipFileDir，即zip解压到这个文件中

    private String zipFileDir; // 上传zip文件夹地址

    private List<Tag> tagList; // 题目标签集合

}
