package com.xiao.xoj.manager.judge;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xiao.xoj.common.exception.CompileError;
import com.xiao.xoj.common.exception.SubmitError;
import com.xiao.xoj.common.exception.SystemError;
import com.xiao.xoj.utils.Constants;
import com.xiao.xoj.utils.JudgeUtils;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/5/3 21:21
 * @description: 负责编译处理
 */
public class Compiler {

    public static String compile(Constants.CompileConfig compileConfig, String code, String language) throws SystemError,CompileError, SubmitError{

        // 调用安全沙箱进行编译
        JSONObject compileResult = SandboxRun.compile(compileConfig.getMaxCpuTime(),
                compileConfig.getMaxRealTime(),
                compileConfig.getMaxMemory(),
                256 * 1024 * 1204L,
                compileConfig.getSrcName(),
                compileConfig.getExeName(),
                parseCompileCommand(compileConfig.getCommand(), compileConfig),
                compileConfig.getEnvs(),
                code,
                true,
                false,
                null
                );
        System.out.println("===compile complete===");
        System.out.println("result = " + JSONUtil.toJsonStr(compileResult));

        // 编译是否通过，0通过，非0未通过
        if (compileResult.getInt("status").intValue() != Constants.Judge.STATUS_ACCEPTED.getStatus()) {
            throw new CompileError("Compile Error.", ((JSONObject)compileResult.get("files")).getStr("stdout"),
                    ((JSONObject)compileResult.get("files")).getStr("stderr"));
        }

        String fileId = ((JSONObject)compileResult.get("fileIds")).getStr(compileConfig.getExeName());
        if (StringUtils.isEmpty(fileId)) {
            throw new SubmitError("Executable file not found.", ((JSONObject) compileResult.get("files")).getStr("stdout"),
                    ((JSONObject) compileResult.get("files")).getStr("stderr"));
        }
        System.out.println("fileId = " + fileId);
        // 返回编译文件的id
        return fileId;
    }

    private static List<String> parseCompileCommand(String command, Constants.CompileConfig compileConfig) {

        command = MessageFormat.format(command, Constants.JudgeDir.TMPFS_DIR.getContent(),
                compileConfig.getSrcName(), compileConfig.getExeName());
        return JudgeUtils.translateCommandline(command);
    }
}
