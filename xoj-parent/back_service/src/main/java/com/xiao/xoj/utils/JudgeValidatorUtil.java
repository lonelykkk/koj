package com.xiao.xoj.utils;

import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.pojo.dto.SubmitJudgeDTO;
import com.xiao.xoj.pojo.dto.TestJudgeDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/4/27 20:51
 * @description: 评测数据校验工具
 */
@Component
public class JudgeValidatorUtil {


    private final static List<String> XOJ_LANGUAGE_LIST = Arrays.asList("C++", "C++ With O2",
            "C", "C With O2", "Python3", "Python2", "Java", "Golang", "C#", "PHP", "PyPy2", "PyPy3",
            "JavaScript Node", "JavaScript V8");

    private static HashMap<String, String> MODE_MAP_LANGUAGE;

    @PostConstruct
    public void init() {
        MODE_MAP_LANGUAGE = new HashMap<>();
        MODE_MAP_LANGUAGE.put("text/x-c++src", "C++ With O2");
        MODE_MAP_LANGUAGE.put("text/x-csrc", "C With O2");
        MODE_MAP_LANGUAGE.put("text/x-java", "Java");
        MODE_MAP_LANGUAGE.put("text/x-go", "Golang");
        MODE_MAP_LANGUAGE.put("text/x-csharp", "C#");
        MODE_MAP_LANGUAGE.put("text/x-php", "PHP");
    }

    // 校验普通评测信息
    public void validatorSubmissionInfo(SubmitJudgeDTO judgeDTO) throws StatusFailException{

        if (!XOJ_LANGUAGE_LIST.contains(judgeDTO.getLanguage())) {
            throw new StatusFailException("提交的代码的语言错误！请使用" + XOJ_LANGUAGE_LIST + "中之一的语言！");
        }

        if (judgeDTO.getCode().length() > 65535) {
            throw new StatusFailException("提交的代码是无效的，代码字符长度请不要超过65535！");
        }
    }

    // 校验测试评测信息
    public void validatorSubmissionTestInfo(TestJudgeDTO testJudgeDTO) {
        if (StringUtils.isEmpty(testJudgeDTO.getCode())) {
            throw new StatusFailException("在线调试的代码不可为空！");
        }

        if (StringUtils.isEmpty(testJudgeDTO.getLanguage())) {
            throw new StatusFailException("在线调试的编程语言不可为空！");
        }

        if (!XOJ_LANGUAGE_LIST.contains(testJudgeDTO.getLanguage())) {
            throw new StatusFailException("提交的代码的语言错误！请使用" + XOJ_LANGUAGE_LIST + "中之一的语言！");
        }

        if (testJudgeDTO.getUserInput().length() > 1000) {
            throw new StatusFailException("在线调试的输入数据字符长度不能超过1000！");
        }

        if (testJudgeDTO.getPid() == null) {
            throw new StatusFailException("在线调试所属题目的id不能为空！");
        }

        if (testJudgeDTO.getCode().length() < 50
                && !testJudgeDTO.getLanguage().contains("Py")
                && !testJudgeDTO.getLanguage().contains("PHP")
                && !testJudgeDTO.getLanguage().contains("JavaScript")) {
            throw new StatusFailException("提交的代码是无效的，代码字符长度请不要低于50！");
        }

        if (testJudgeDTO.getCode().length() > 65535) {
            throw new StatusFailException("提交的代码是无效的，代码字符长度请不要超过65535！");
        }
    }

}
