package com.xiao.xoj.manager.judge;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xiao.xoj.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/5/3 21:22
 * @description: TODO
 */
@Slf4j(topic = "xoj")
public class SandboxRun {

    private static final RestTemplate restTemplate;

    // 单例模式
    private static final SandboxRun instance = new SandboxRun();

//    private static final String SANDBOX_BASE_URL = "http://114.55.134.193:8080";
    private static final String SANDBOX_BASE_URL = "http://localhost:5050";

    public static final HashMap<String, Integer> RESULT_MAP_STATUS = new HashMap<>();

    public static final JSONArray COMPILE_FILES = new JSONArray();

    private static final int maxProcessNumber = 128;

    private static final int TIME_LIMIT_MS = 16000;

    private static final int MEMORY_LIMIT_MB = 512;

    private static final int STACK_LIMIT_MB = 128;

    private static final int STDIO_SIZE_MB = 32;

    private SandboxRun() {

    }

    static {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(20000);
        requestFactory.setReadTimeout(180000);
        restTemplate = new RestTemplate(requestFactory);
    }

    static {
        // 程序在资源限制内正常退出
        RESULT_MAP_STATUS.put("Accepted", Constants.Judge.STATUS_ACCEPTED.getStatus());
        // 超出内存限制
        RESULT_MAP_STATUS.put("Memory Limit Exceeded", Constants.Judge.STATUS_MEMORY_LIMIT_EXCEEDED.getStatus());
        // 超出 timeLimit 时间限制 或者 超过 clockLimit 等待时间限制
        RESULT_MAP_STATUS.put("Time Limit Exceeded", Constants.Judge.STATUS_TIME_LIMIT_EXCEEDED.getStatus());
        // 超出 pipeCollector 限制 或者 超出 -output-limit 最大输出限制
        RESULT_MAP_STATUS.put("Output Limit Exceeded", Constants.Judge.STATUS_OUTPUT_LIMIT_EXCEEDED.getStatus());
        // copyIn 指定文件不存在 或者 copyIn 指定文件大小超出沙箱文件系统限制 或者 copyOut 指定文件不存在
        RESULT_MAP_STATUS.put("File Error", Constants.Judge.STATUS_SYSTEM_ERROR.getStatus());
        // 程序用非 0 返回值退出
        RESULT_MAP_STATUS.put("Nonzero Exit Status", Constants.Judge.STATUS_RUNTIME_ERROR.getStatus());
        // 程序收到结束信号而退出（例如 SIGSEGV）
        RESULT_MAP_STATUS.put("Signalled", Constants.Judge.STATUS_RUNTIME_ERROR.getStatus());
        // 指定程序路径不存在
        // 或者容器创建失败 比如使用非特权 docker 或者在个人目录下以 root 权限运行
        // 或者其他错误
        RESULT_MAP_STATUS.put("Internal Error", Constants.Judge.STATUS_SYSTEM_ERROR.getStatus());
    }

    static {
        JSONObject content = new JSONObject();
        content.set("content", "");

        JSONObject stdout = new JSONObject();
        stdout.set("name", "stdout");
        stdout.set("max", 1024 * 1024 * STDIO_SIZE_MB);

        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * STDIO_SIZE_MB);

        COMPILE_FILES.put(content);
        COMPILE_FILES.put(stdout);
        COMPILE_FILES.put(stderr);
    }

    public static final List<String> signals = Arrays.asList(
            "", // 0
            "Hangup", // 1
            "Interrupt", // 2
            "Quit", // 3
            "Illegal instruction", // 4
            "Trace/breakpoint trap", // 5
            "Aborted", // 6
            "Bus error", // 7
            "Floating point exception", // 8
            "Killed", // 9
            "User defined signal 1", // 10
            "Segmentation fault", // 11
            "User defined signal 2", // 12
            "Broken pipe", // 13
            "Alarm clock", // 14
            "Terminated", // 15
            "Stack fault", // 16
            "Child exited", // 17
            "Continued", // 18
            "Stopped (signal)", // 19
            "Stopped", // 20
            "Stopped (tty input)", // 21
            "Stopped (tty output)", // 22
            "Urgent I/O condition", // 23
            "CPU time limit exceeded", // 24
            "File size limit exceeded", // 25
            "Virtual timer expired", // 26
            "Profiling timer expired", // 27
            "Window changed", // 28
            "I/O possible", // 29
            "Power failure", // 30
            "Bad system call" // 31
    );

    public JSONArray run(String uri, JSONObject param) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(JSONUtil.toJsonStr(param), headers);
        ResponseEntity<String> postForEntity;
        try {
            System.out.println("sandbox run");
            postForEntity = restTemplate.postForEntity(SANDBOX_BASE_URL + uri, request, String.class);
            return JSONUtil.parseArray(postForEntity.getBody());
        } catch (RestClientResponseException ex) {
            if (ex.getRawStatusCode() != 200) {
//                throw new SystemException("");
            }
        } catch (Exception e) {

        }
        return null;
    }

    // 删除代码文件
    public static void delFile(String fileId) {
        try {
            restTemplate.delete(SANDBOX_BASE_URL + "/file/{0}", fileId);
        } catch (RestClientResponseException ex) {
            if (ex.getRawStatusCode() != 200) {
                log.error("安全沙箱判题的删除内存中的文件缓存操作异常----------------->{}", ex.getResponseBodyAsString());
            }
        }
    }



    public static JSONObject compile(Long maxCpuTime,
                                    Long maxRealTime,
                                    Long maxMemory,
                                    Long maxStack,
                                    String srcName,
                                    String exeName,
                                    List<String> args,
                                    List<String> envs,
                                    String code,
                                    Boolean needCopyOutCached,
                                    Boolean needCopyOutExe,
                                    String copyOutDir) {
        JSONObject cmd = new JSONObject();
        cmd.set("args", args);
        cmd.set("env", envs);
        cmd.set("files", COMPILE_FILES);
        // ms --> ns
        cmd.set("cpuLimit", maxCpuTime * 1000 * 1000L);
        cmd.set("clockLimit", maxRealTime * 1000 * 1000L);
        // byte
        cmd.set("memoryLimit", maxMemory);
        cmd.set("stackLimit", maxStack);
        cmd.set("procLimit", maxProcessNumber);

        JSONObject fileContent = new JSONObject();
        fileContent.set("content", code);

        JSONObject copyIn = new JSONObject();
        copyIn.set(srcName, fileContent);

        cmd.set("copyIn", copyIn);
        cmd.set("copyOut", new JSONArray().put("stdout").put("stderr"));

        if (needCopyOutCached) {
            cmd.set("copyOutCached", new JSONArray().put(exeName));
        }

        if (needCopyOutExe) {
            cmd.set("copyOutDir", copyOutDir);
        }

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));
        System.out.println("compile json data = " + JSONUtil.toJsonStr(param));
        JSONArray result = instance.run("/run", param);

        JSONObject compileRes = (JSONObject) result.get(0);
        compileRes.set("originalStatus", compileRes.getStr("status"));
        compileRes.set("status", RESULT_MAP_STATUS.get(compileRes.getStr("originalStatus")));
        return compileRes;
    }

    /**
     * @des 普通评测
     *
     * @param maxTime               评测的最大限制时间 ms
     * @param maxMemory             评测的最大内存大小 kb
     * @param maxStack              评测的最大限制栈空间 mb
     * @param maxOutputSize         评测的最大输出大小 kb
     * @param exeName               评测的用户程序名称
     * @param testCasePath          题目数据的输入文件路径
     * @param testCaseContent       题目数据的输入数据
     * @param fileId                评测的用户程序文件id
     * @param fileContent           评测的用户程序文件内容
     * @param args                  普通评测运行cmd的命令参数
     * @param envs                  普通评测运行的环境变量
     * @return JSONObject
     */
    public static JSONObject testCase(Long maxTime,
                                     Long maxMemory,
                                     Integer maxStack,
                                     Long maxOutputSize,
                                     String exeName,
                                     String testCasePath,
                                     String testCaseContent,
                                     String fileId,
                                     String fileContent,
                                     List<String> args,
                                     List<String> envs) {

        JSONObject cmd = new JSONObject();

        cmd.set("args", args);
        cmd.set("envs", envs);

        JSONArray files = new JSONArray();
        JSONObject content = new JSONObject();
        // 如果testCasePath != null，优先使用绝对路径
        if (!StringUtils.isEmpty(testCasePath)) {
            // 文件绝对路径，LocalFile
            content.set("src", testCasePath);
        } else {
            // 文件内容，MemoryFile
            content.set("content", testCaseContent);
        }

        JSONObject stdout = new JSONObject();
        stdout.set("name", "stdout");
        stdout.set("max", maxOutputSize);

        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * 16);
        files.put(content);
        files.put(stdout);
        files.put(stderr);

        cmd.set("files", files);

        // 毫秒转纳秒
        cmd.set("cpuLimit", maxTime * 1000 * 1000L);
        cmd.set("clockLimit", maxTime * 1000 * 1000L * 3);  // 等待时间限制

        // mb --> kb
        cmd.set("memoryLimit", (maxMemory + 100) * 1024 * 1024L);
        cmd.set("procLimit", maxProcessNumber);
        cmd.set("stackLimit", maxStack * 1024 * 1024L);

        JSONObject exeFile = new JSONObject();
        if (!StringUtils.isEmpty(fileId)) {
            exeFile.set("fileId", fileId);
        } else {
            exeFile.set("content", fileContent);
        }
        JSONObject copyIn = new JSONObject();
        copyIn.set(exeName, exeFile);

        cmd.set("copyIn", copyIn);
        cmd.set("copyOut", new JSONArray().put("stdout").put("stderr"));

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));

        System.out.println("run testcase json data = " + JSONUtil.toJsonStr(param));

        // 调用判题沙箱
        JSONArray result = instance.run("/run", param);
        JSONObject testcaseResult = (JSONObject) result.get(0);
        testcaseResult.set("originalStatus", testcaseResult.getStr("status"));
        testcaseResult.set("status", RESULT_MAP_STATUS.get(testcaseResult.getStr("originalStatus")));
        return testcaseResult;
    }


}
/*
1. compile JSON Request body：编译代码的请求体格式
{
    "cmd": [{
        "args": ["/usr/bin/g++", "a.cc", "-o", "a"],
        "env": ["PATH=/usr/bin:/bin"],
        "files": [{
            "content": ""
        }, {
            "name": "stdout",
            "max": 10240
        }, {
            "name": "stderr",
            "max": 10240
        }],
        "cpuLimit": 10000000000,
        "memoryLimit": 104857600,
        "procLimit": 50,
        "copyIn": {
            "a.cc": {
                "content": "#include <iostream>\nusing namespace std;\nint main() {\nint a, b;\ncin >> a >> b;\ncout << a + b << endl;\n}"
            }
        },
        "copyOut": ["stdout", "stderr"],
        "copyOutCached": ["a.cc", "a"],
        "copyOutDir": "1"
    }]
}

2. compile JSON Response body：编译代码的响应体格式
[
    {
        "status": "Accepted",
        "exitStatus": 0,
        "time": 303225231,
        "memory": 32243712,
        "runTime": 524177700,
        "files": {
            "stderr": "",
            "stdout": ""
        },
        "fileIds": {
            "a": "5LWIZAA45JHX4Y4Z",
            "a.cc": "NOHPGGDTYQUFRSLJ"
        }
    }
]

3. run JSON Request body：评测测试用例请求体格式
{
    "cmd": [{
        "args": ["a"],
        "env": ["PATH=/usr/bin:/bin"],
        "files": [{
            "content": "1 1"
        }, {
            "name": "stdout",
            "max": 10240
        }, {
            "name": "stderr",
            "max": 10240
        }],
        "cpuLimit": 10000000000,
        "memoryLimit": 104857600,
        "procLimit": 50,
        "strictMemoryLimit": false,
        "copyIn": {
            "a": {
                "fileId": "5LWIZAA45JHX4Y4Z"
            }
        }
    }]
}

4. run JSON Response body：评测测试用例响应体格式
[
    {
        "status": "Accepted",
        "exitStatus": 0,
        "time": 1173000,
        "memory": 10637312,
        "runTime": 1100200,
        "files": {
            "stderr": "",
            "stdout": "2\n"
        }
    }
]


 {
    "cmd":[{
        "args":["/w/main"],
        "envs":["PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin","LANG=en_US.UTF-8","LC_ALL=en_US.UTF-8","LANGUAGE=en_US:en","HOME=/w"],
        "files":[
            {"src":"/judge/testcase/problem_6/1.in"},
            {"name":"stdout","max":33554432},
            {"name":"stderr","max":16777216}
        ],
            "cpuLimit":1000000000,
            "clockLimit":3000000000,
            "memoryLimit":373293056,
            "procLimit":128,
            "stackLimit":134217728,
            "copyIn":
                {"main":
                    {"fileId":"NRQDWBUX"}
                },
            "copyOut":["stdout","stderr"]
           }]
   }
*/
