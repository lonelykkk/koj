package com.xiao.xoj.manager.judge.task;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xiao.xoj.manager.judge.SandboxRun;
import com.xiao.xoj.manager.judge.entity.JudgeDTO;
import com.xiao.xoj.manager.judge.entity.JudgeGlobalDTO;
import com.xiao.xoj.manager.judge.entity.SandBoxRes;
import com.xiao.xoj.utils.Constants;
import com.xiao.xoj.utils.JudgeUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author 肖恩
 * @create 2023/5/20 17:17
 * @description: 在线自测
 */
@Component
public class TestJudge {

    private final static Pattern EOL_PATTERN = Pattern.compile("[^\\S\\n]+(?=\\n)");

    public JSONObject judgeCase(JudgeDTO judgeDTO, JudgeGlobalDTO judgeGlobalDTO) {
        Constants.RunConfig runConfig = judgeGlobalDTO.getRunConfig();
        // 调用安全沙箱使用测试点对程序进行测试
        JSONObject judgeResult = SandboxRun.testCase(
                judgeGlobalDTO.getMaxTime(),
                judgeGlobalDTO.getMaxMemory(),
                judgeGlobalDTO.getMaxStack(),
                judgeDTO.getMaxOutputSize(),
                runConfig.getExeName(),
                judgeDTO.getTestCaseInputFilePath(),
                judgeDTO.getTestCaseInputFileContent(),
                judgeGlobalDTO.getUserFileId(),
                judgeGlobalDTO.getUserFileContent(),
                parseRunCommand(runConfig.getCommand(), runConfig, null, null, null),
                runConfig.getEnvs()
        );
        return process(judgeDTO, judgeGlobalDTO, judgeResult);
    }

    // 对评测后的数据进行封装
    private JSONObject process(JudgeDTO judgeDTO, JudgeGlobalDTO judgeGlobalDTO, JSONObject judgeResult) {
        System.out.println("该用例评测结束，进行评测结构的数据封装");
        System.out.println("judgeResult = " + JSONUtil.toJsonStr(judgeResult));

        SandBoxRes sandBoxRes = SandBoxRes.builder()
                .status(judgeResult.getInt("status"))
                .originalStatus(judgeResult.getStr("originalStatus"))
                .exitCode(judgeResult.getInt("exitStatus"))
                .memory(judgeResult.getLong("memory") / 1024) // b --> kb
                .time(judgeResult.getLong("time") / 1000000) // ns --> ms
                .stdout(((JSONObject) judgeResult.get("files")).getStr("stdout"))
                .stderr(((JSONObject) judgeResult.get("files")).getStr("stderr"))
                .build();

        return checkResult(judgeDTO, judgeGlobalDTO, sandBoxRes);
    }


    private JSONObject checkResult(JudgeDTO judgeDTO, JudgeGlobalDTO judgeGlobalDTO, SandBoxRes sandBoxRes) {
        JSONObject result = new JSONObject();
        StringBuilder errMsg = new StringBuilder(); // 错误信息

        // 判断用例运行结果
        if (sandBoxRes.getStatus().equals(Constants.Judge.STATUS_ACCEPTED.getStatus())) {
            // 运行无异常，判断相关限制：时间限制，内存限制
            // 对结果的时间损耗和空间损耗与题目限制做比较，判断是否mle和tle
            if (sandBoxRes.getTime() > judgeGlobalDTO.getMaxTime()) {
                result.set("status", Constants.Judge.STATUS_TIME_LIMIT_EXCEEDED.getStatus());
            } else if (sandBoxRes.getMemory() > judgeGlobalDTO.getMaxMemory() * 1024) {
                result.set("status", Constants.Judge.STATUS_MEMORY_LIMIT_EXCEEDED.getStatus());
            } else {
                // 判断该测试样例是否有输出，如为提供输出，则表示通过
                if (judgeDTO.getTestCaseOutputContent() != null) {
                    // 判断测试样例结果是否需要取出末尾空白符
                    if (judgeGlobalDTO.getRemoveEOLBlank() != null && judgeGlobalDTO.getRemoveEOLBlank()) {
                        String stdOut = rtrim(sandBoxRes.getStdout());
                        String testCaseOutput = rtrim(judgeDTO.getTestCaseOutputContent());
                        if (Objects.equals(stdOut, testCaseOutput)) {
                            result.set("status", Constants.Judge.STATUS_ACCEPTED.getStatus());
                        } else {
                            result.set("status", Constants.Judge.STATUS_WRONG_ANSWER.getStatus());
                        }
                    } else {
                        // 直接比较答案
                        if (Objects.equals(sandBoxRes.getStdout(), judgeDTO.getTestCaseOutputContent())) {
                            result.set("status", Constants.Judge.STATUS_ACCEPTED.getStatus());
                        } else {
                            result.set("status", Constants.Judge.STATUS_WRONG_ANSWER.getStatus());
                        }
                    }
                } else {
                    result.set("status", Constants.Judge.STATUS_ACCEPTED.getStatus());
                }
            }
        } else if (sandBoxRes.getStatus().equals(Constants.Judge.STATUS_TIME_LIMIT_EXCEEDED.getStatus())) {
            // 时间超限
            result.set("status", Constants.Judge.STATUS_TIME_LIMIT_EXCEEDED.getStatus());
            errMsg.append(Constants.Judge.STATUS_TIME_LIMIT_EXCEEDED.getName());
        } else if (sandBoxRes.getStatus().equals(Constants.Judge.STATUS_MEMORY_LIMIT_EXCEEDED.getStatus())) {
            // 内存超限
            result.set("status", Constants.Judge.STATUS_MEMORY_LIMIT_EXCEEDED.getStatus());
            errMsg.append(Constants.Judge.STATUS_MEMORY_LIMIT_EXCEEDED.getName());
        } else if (sandBoxRes.getStatus().equals(Constants.Judge.STATUS_OUTPUT_LIMIT_EXCEEDED.getStatus())) {
            // 输出超限
            result.set("status", Constants.Judge.STATUS_OUTPUT_LIMIT_EXCEEDED.getStatus());
            errMsg.append(Constants.Judge.STATUS_OUTPUT_LIMIT_EXCEEDED.getName());
        } else if (sandBoxRes.getStatus().equals(Constants.Judge.STATUS_RUNTIME_ERROR.getStatus())) {
            // 运行错误
            result.set("status", Constants.Judge.STATUS_RUNTIME_ERROR.getStatus());
            errMsg.append(Constants.Judge.STATUS_RUNTIME_ERROR.getName());
            if (sandBoxRes.getExitCode() < 32) {
                errMsg.append(String.format("The program return exit status code: %s (%s)\n", sandBoxRes.getExitCode(), SandboxRun.signals.get(sandBoxRes.getExitCode())));
            } else {
                errMsg.append(String.format("The program return exit status code: %s\n", sandBoxRes.getExitCode()));
            }
        } else if (sandBoxRes.getStatus().equals(Constants.Judge.STATUS_SYSTEM_ERROR.getStatus())) {
            // 系统错误
            result.set("status", Constants.Judge.STATUS_SYSTEM_ERROR.getStatus());
            errMsg.append(Constants.Judge.STATUS_SYSTEM_ERROR.getName());
        } else {
            // 其他
            result.set("status", sandBoxRes.getStatus());
            errMsg.append("other unknow error");
        }

        result.set("memory", sandBoxRes.getMemory());
        result.set("time", sandBoxRes.getTime());

        if (!StringUtils.isEmpty(sandBoxRes.getStderr())) {
            errMsg.append(sandBoxRes.getStderr());
        }

        // 记录该测试点的错误信息
        if (!StringUtils.isEmpty(errMsg.toString())) {
            String str = errMsg.toString();
            result.set("errMsg", str.substring(0, Math.min(1024 * 1024, str.length())));
        }

        // 记录该测试点的运行输出
        if (!StringUtils.isEmpty(sandBoxRes.getStdout()) && sandBoxRes.getStdout().length() > 1000) {
            result.set("output", sandBoxRes.getStdout().substring(0, 1000) + "...");
        } else {
            result.set("output", sandBoxRes.getStdout());
        }

        return result;
    }

    protected static List<String> parseRunCommand(String command,
                                                  Constants.RunConfig runConfig,
                                                  String testCaseInputName,
                                                  String userOutputName,
                                                  String testCaseOutputName) {

        command = MessageFormat.format(command, Constants.JudgeDir.TMPFS_DIR.getContent(),
                runConfig.getExeName(), Constants.JudgeDir.TMPFS_DIR.getContent() + File.separator + testCaseInputName,
                Constants.JudgeDir.TMPFS_DIR.getContent() + File.separator + userOutputName,
                Constants.JudgeDir.TMPFS_DIR.getContent() + File.separator + testCaseOutputName);

        return JudgeUtils.translateCommandline(command);
    }

    // 移除字符串末尾的空白符
    protected String rtrim(String value) {
        if (value == null) return null;
        return EOL_PATTERN.matcher(StrUtil.trimEnd(value)).replaceAll("");
    }
}
