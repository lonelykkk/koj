package com.xiao.xoj.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author 肖恩
 * @create 2023/5/3 21:24
 * @description: 负责题目测试数据的检查与初始化
 */
@Component
public class ProblemTestCaseUtils {

    /**
     * @des：获取指定题目的info数据
     *
     * @param problemId
     * @param testcaseDir
     * @param caseVersion
     * @return
     */
    public JSONObject loadTestCaseInfo(Integer problemId, String testcaseDir, String caseVersion) {
        // 判断info文件是否存在
        if (FileUtil.exist(testcaseDir + File.separator + "info")) {
            FileReader fileReader = new FileReader(testcaseDir + File.separator + "info", CharsetUtil.UTF_8);
            String infoStr = fileReader.readString();
            JSONObject testcaseInfo = JSONUtil.parseObj(infoStr);
            // 测试样例被改动需要重新生成
            if (!testcaseInfo.getStr("version", null).equals(caseVersion)) {
                return tryInitTestCaseInfo(testcaseDir, problemId, caseVersion);
            }
            return testcaseInfo;
        } else {
            return tryInitTestCaseInfo(testcaseDir, problemId, caseVersion);
        }
    }

    // 若没有测试数据，则尝试从数据库获取并且初始化到本地，如果数据库中该题目测试数据为空，rsync同步也出了问题，则直接判系统错误
    private JSONObject tryInitTestCaseInfo(String testcaseDir, Integer problemId, String caseVersion) {
        // 考核途中修改了测试用例，如果用户端没有获取到最新的测试用例（没有刷新页面），那么用户无法通过评测
        // 所以考核途中修改了测试用例，需要提醒用户刷新页面
        // todo 也可以动态的获取最新的测试用例，不需要用户刷新页面，这个功能为完成
        return null;
    }

}
