package com.xiao.xoj.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/3/21 21:14
 * @description: 常量枚举类
 */
public class Constants {

    /**
     * @Description 提交评测结果的状态码
     * @Since 2021/1/1
     */
    public enum Judge {
        // 未提交
        STATUS_NOT_SUBMITTED(-10, "Not Submitted", null),
        STATUS_SUBMITTED_UNKNOWN_RESULT(-5, "Submitted Unknown Result", null),
        // 评测取消
        STATUS_CANCELLED(-4, "Cancelled","ca"),
        // 输出格式错误
        STATUS_PRESENTATION_ERROR(-3, "Presentation Error", "pe"),
        // 编译错误
        STATUS_COMPILE_ERROR(-2, "Compile Error", "ce"),
        // 答案错误
        STATUS_WRONG_ANSWER(-1, "Wrong Answer", "wa"),
        // 评测通过
        STATUS_ACCEPTED(0, "Accepted", "ac"),
        // 时间超限
        STATUS_TIME_LIMIT_EXCEEDED(1, "Time Limit Exceeded", "tle"),
        // 空间超限
        STATUS_MEMORY_LIMIT_EXCEEDED(2, "Memory Limit Exceeded", "mle"),
        // 运行错误
        STATUS_RUNTIME_ERROR(3, "Runtime Error", "re"),
        // 系统错误
        STATUS_SYSTEM_ERROR(4, "System Error", "se"),
        // 排队中
        STATUS_PENDING(5, "Pending", null),
        // 编译中
        STATUS_COMPILING(6, "Compiling", null),
        // 评测中
        STATUS_JUDGING(7, "Judging", null),
        // 部分通过
        STATUS_PARTIAL_ACCEPTED(8, "Partial Accepted", "pa"),
        // 提交中
        STATUS_SUBMITTING(9, "Submitting", null),
        // 提交失败
        STATUS_SUBMITTED_FAILED(10, "Submitted Failed", null),
        // 输出超出
        STATUS_OUTPUT_LIMIT_EXCEEDED(11, "Output Limit Exceeded", "ole"),
        STATUS_NULL(15, "No Status", null),
        JUDGE_SERVER_SUBMIT_PREFIX(-1002, "Judge SubmitId-ServerId:", null);

        private Judge(Integer status, String name, String columnName) {
            this.status = status;
            this.name = name;
            this.columnName = columnName;
        }

        private final Integer status;
        private final String name;
        private final String columnName;

        public Integer getStatus() {
            return status;
        }

        public String getName() {
            return name;
        }

        public String getColumnName() {
            return columnName;
        }
    }


    public enum Queue {
        // 考核评测队列
        CONTEST_JUDGE_WAITING("Contest_Waiting_Handle_Queue"),
        // 普通评测队列
        GENERAL_JUDGE_WAITING("General_Waiting_Handle_Queue"),
        // 在线调试评测队列
        TEST_JUDGE_WAITING("Test_Judge_Waiting_Handle_Queue");

        private Queue(String name) {
            this.name = name;
        }
        private final String name;
        public String getName() {
            return name;
        }
    }


    /**
     * @des：评测类型
     */
    public enum JudgeMode {
        TEST("test"),
        DEFAULT("default"),
        SPJ("spj"),
        INTERACTIVE("interactive");

        private final String mode;

        JudgeMode(String mode) {
            this.mode = mode;
        }

        public String getMode() {
            return mode;
        }

        public static JudgeMode getJudgeMode(String mode){
            for (JudgeMode judgeMode : JudgeMode.values()) {
                if (judgeMode.getMode().equals(mode)) {
                    return judgeMode;
                }
            }
            return null;
        }
    }



    /**
     * 账户相关常量
     */
    public enum Account {
        CODE_CHANGE_PASSWORD_FAIL("change-password-fail:"),
        CODE_CHANGE_PASSWORD_LOCK("change-password-lock:"),
        CODE_ACCOUNT_LOCK("account-lock:"),
        CODE_CHANGE_EMAIL_FAIL("change-email-fail:"),
        CODE_CHANGE_EMAIL_LOCK("change-email-lock:"),

        TRY_LOGIN_NUM("try-login-num:"),

        SUPER_ADMIN_UID_LIST_CACHE("super_admin_uid_list_case"),

        SUBMIT_JUDGE_LOCK("submit_judge_lock:"),
        TEST_JUDGE_LOCK("test_judge_lock:"),
        SUBMIT_CONTEST_LOCK("submit_contest_lock:"),
        DISCUSSION_ADD_NUM_LOCK("discussion_add_num_lock:");

        private final String code;

        Account(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * 邮件任务的一些常量
     */
    public enum Email {

        OJ_URL("OJ_UR"),
        OJ_NAME("OJ_NAME"),
        OJ_SHORT_NAME("OJ_SHORT_NAME"),
        EMAIL_FROM("EMAIL_FROM"),
        EMAIL_BACKGROUND_IMG("EMAIL_BACKGROUND_IMG"),
        REGISTER_KEY_PREFIX("register-user:"),
        RESET_PASSWORD_KEY_PREFIX("reset-password:"),
        RESET_EMAIL_PREFIX("reset-email:"),
        RESET_EMAIL_LOCK("reset-email-lock:"),
        REGISTER_EMAIL_LOCK("register-email-lock:");

        private final String value;

        Email(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * @des: 文件相关常量
     */
    public enum File {
        // zip临时存储：file/tmp
//        TESTCASE_TMP_FOLDER("/file/tmp"),

        // testcase保存地址：file/testcase
//        TESTCASE_BASE_FOLDER("/file/testcase"),

//        // zip临时存储， 测试
        TESTCASE_TMP_FOLDER("D:\\MyCode\\MyProject\\Graduation\\code\\xoj-parent\\back_service\\file\\tmp"),
//
//        // testcase保存地址，测试
        TESTCASE_BASE_FOLDER("D:\\MyCode\\MyProject\\Graduation\\code\\xoj-parent\\back_service\\file\\testcase"),

        // 题目保存地址：file/problem
        PROBLEM_FILE_FOLDER("D:\\MyCode\\MyProject\\Graduation\\code\\xoj-parent\\back_service\\file\\problem");


        private final String path;

        File(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    /**
     * @des: 考核相关常量
     */
    public enum Contest {
        TYPE_ACM(0, "ACM"),  // ACM类型考核
        TYPE_OI(1, "OI"),    // OI类型考核

        STATUS_SCHEDULED(-1, "Scheduled"), // 考核未开始
        STATUS_RUNNING(0, "Running"),      // 考核进行中
        STATUS_ENDED(1, "Ended"),          // 考核已结束

        AUTH_PUBLIC(0, "Public"),   // 公开考核
        AUTH_PRIVATE(1, "Private"), // 私有考核

        RECORD_NOT_AC_PENALTY(-1, "未AC通过算罚时"),
        RECORD_NOT_AC_NOT_PENALTY(0, "未AC通过不罚时"),
        RECORD_AC(1, "AC通过");

        private final Integer code;

        private final String name;

        Contest(Integer code, String name){
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

    }


    /**
     * @des: 评测任务类型
     */
    public enum TaskType{

        // 自身评测
        JUDGE("/judge"),

        // 在线调试
        TEST_JUDGE("/test-judge");

        private final String path;

        TaskType(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

    }

    public enum JudgeDir {

        RUN_WORKPLACE_DIR("/judge/run"),

        TEST_CASE_DIR("/judge/test_case"),

        SPJ_WORKPLACE_DIR("/judge/spj"),

        INTERACTIVE_WORKPLACE_DIR("/judge/interactive"),

        TMPFS_DIR("/w");


        private final String content;

        JudgeDir(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

    /*
     * PATH：系统的环境变量，可以执行 echo $PATH 查看
     * LANG：系统的编码语言，可以执行 echo $LANG 查看
     * LC_ALL：定义整体语系
     * LANGUAGE：设置应用程序的界面语言
     * HOME：指定当前用户的主目录，相关文件会保存到这个文件夹下
     */
    public static List<String> defaultEnv = Arrays.asList(
            "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin",
            "LANG=en_US.UTF-8",
            "LC_ALL=en_US.UTF-8",
            "LANGUAGE=en_US:en",
            "HOME=/w");


    public static List<String> python3Env = Arrays.asList("LANG=en_US.UTF-8",
            "LANGUAGE=en_US:en", "LC_ALL=en_US.UTF-8", "PYTHONIOENCODING=utf-8");

    // todo 未配置
    public static List<String> golangEnv = Arrays.asList("GODEBUG=madvdontneed=1",
            "GOCACHE=off", "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
            "LANG=en_US.UTF-8", "LANGUAGE=en_US:en", "LC_ALL=en_US.UTF-8");

    /*
            {0} --> tmpfs_dir
            {1} --> srcName
            {2} --> exeName
     */

    public enum CompileConfig {
        C("C", "main.c", "main", 3000L, 10000L, 256 * 1024 * 1024L, "/usr/bin/gcc -shared -fPIC -DONLINE_JUDGE -w -fmax-errors=1 -std=c11 {1} -lm -o {2}", defaultEnv),

        CWithO2("C With O2", "main.c", "main", 3000L, 10000L, 256 * 1024 * 1024L, "/usr/bin/gcc -DONLINE_JUDGE -O2 -w -fmax-errors=1 -std=c11 {1} -lm -o {2}", defaultEnv),

        CPP("C++", "main.cpp", "main", 10000L, 20000L, 512 * 1024 * 1024L, "/usr/bin/g++ -DONLINE_JUDGE -w -fmax-errors=1 -std=c++14 {1} -lm -o {2}", defaultEnv),

        CPPWithO2("C++ With O2", "main.cpp", "main", 10000L, 20000L, 512 * 1024 * 1024L, "/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=1 -std=c++14 {1} -lm -o {2}", defaultEnv),

        JAVA("Java", "Main.java", "Main.jar", 10000L, 20000L, 512 * 1024 * 1024L, "/bin/bash -c \"javac -encoding utf-8 {1} && jar -cvf {2} *.class\"", defaultEnv),

        PYTHON2("Python2", "main.py", "main.pyc", 3000L, 10000L, 128 * 1024 * 1024L, "/usr/bin/python -m py_compile ./{1}", defaultEnv),

        PYTHON3("Python3", "main.py", "__pycache__/main.cpython-37.pyc", 3000L, 10000L, 128 * 1024 * 1024L, "/usr/bin/python3.7 -m py_compile ./{1}", defaultEnv),

        GOLANG("Golang", "main.go", "main", 3000L, 5000L, 512 * 1024 * 1024L, "/usr/bin/go build -o {2} {1}", defaultEnv),

        CS("C#", "Main.cs", "main", 5000L, 10000L, 512 * 1024 * 1024L, "/usr/bin/mcs -optimize+ -out:{0}/{2} {0}/{1}", defaultEnv),

        PyPy2("PyPy2", "main.py", "__pycache__/main.pypy-73.pyc", 3000L, 10000L, 256 * 1024 * 1024L, "/usr/bin/pypy -m py_compile {0}/{1}", defaultEnv),

        PyPy3("PyPy3", "main.py", "__pycache__/main.pypy38.pyc", 3000L, 10000L, 256 * 1024 * 1024L, "/usr/bin/pypy3 -m py_compile {0}/{1}", defaultEnv),

        SPJ_C("SPJ-C", "spj.c", "spj", 3000L, 5000L, 512 * 1024 * 1024L, "/usr/bin/gcc -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c99 {1} -lm -o {2}", defaultEnv),

        SPJ_CPP("SPJ-C++", "spj.cpp", "spj", 10000L, 20000L, 512 * 1024 * 1024L, "/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c++14 {1} -lm -o {2}", defaultEnv),

        INTERACTIVE_C("INTERACTIVE-C", "interactive.c", "interactive", 3000L, 5000L, 512 * 1024 * 1024L, "/usr/bin/gcc -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c99 {1} -lm -o {2}", defaultEnv),

        INTERACTIVE_CPP("INTERACTIVE-C++", "interactive.cpp", "interactive", 10000L, 20000L, 512 * 1024 * 1024L, "/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c++14 {1} -lm -o {2}", defaultEnv);

        private final String language;
        private final String srcName;
        private final String exeName;
        private final Long maxCpuTime;
        private final Long maxRealTime;
        private final Long maxMemory;
        private final String command;
        private final List<String> envs;

        CompileConfig(String language, String srcName, String exeName, Long maxCpuTime, Long maxRealTime, Long maxMemory,
                      String command, List<String> envs) {
            this.language = language;
            this.srcName = srcName;
            this.exeName = exeName;
            this.maxCpuTime = maxCpuTime;
            this.maxRealTime = maxRealTime;
            this.maxMemory = maxMemory;
            this.command = command;
            this.envs = envs;
        }

        public String getLanguage() {
            return language;
        }

        public String getSrcName() {
            return srcName;
        }

        public String getExeName() {
            return exeName;
        }

        public Long getMaxCpuTime() {
            return maxCpuTime;
        }

        public Long getMaxRealTime() {
            return maxRealTime;
        }

        public Long getMaxMemory() {
            return maxMemory;
        }

        public String getCommand() {
            return command;
        }

        public List<String> getEnvs() {
            return envs;
        }

        public static CompileConfig getCompilerByLanguage(String language) {
            for (CompileConfig compileConfig : CompileConfig.values()) {
                if (compileConfig.getLanguage().equals(language)) {
                    return compileConfig;
                }
            }
            return null;
        }
    }


    /*
    {0} --> tmpfs_dir
    {1} --> exeName (user or spj)
    {2} --> The test case standard input file name of question
    {3} --> The user's program output file name of question
    {4} --> The test case standard output file name of question
*/
    /**
     * @des 运行相关配置
     *
     * @return
     */
    public enum RunConfig {
        C("C", "{0}/{1}", "main", defaultEnv),

        CWithO2("C With O2", "{0}/{1}", "main", defaultEnv),

        CPP("C++", "{0}/{1}", "main", defaultEnv),

        CPPWithO2("C++ With O2", "{0}/{1}", "main", defaultEnv),

        JAVA("Java", "/usr/java/jdk1.8.0_121/bin/java -Dfile.encoding=UTF-8 -cp {0}/{1} Main", "Main.jar", defaultEnv),

        PYTHON2("Python2", "/usr/bin/python {1}", "main", defaultEnv),

        PYTHON3("Python3", "/usr/bin/python3.7 {1}", "main", python3Env),

        GOLANG("Golang", "{0}/{1}", "main", golangEnv),

        CS("C#", "/usr/bin/mono {0}/{1}", "main", defaultEnv),

        PyPy2("PyPy2", "/usr/bin/pypy {1}", "main.pyc", defaultEnv),

        PyPy3("PyPy3", "/usr/bin/pypy3 {1}", "main.pyc", python3Env),

        PHP("PHP", "/usr/bin/php {1}", "main.php", defaultEnv),

        JS_NODE("JavaScript Node", "/usr/bin/node {1}", "main.js", defaultEnv),

        JS_V8("JavaScript V8", "/usr/bin/jsv8/d8 {1}", "main.js", defaultEnv),

        SPJ_C("SPJ-C", "{0}/{1} {2} {3} {4}", "spj", defaultEnv),

        SPJ_CPP("SPJ-C++", "{0}/{1} {2} {3} {4}", "spj", defaultEnv),

        INTERACTIVE_C("INTERACTIVE-C", "{0}/{1} {2} {3} {4}", "interactive", defaultEnv),

        INTERACTIVE_CPP("INTERACTIVE-C++", "{0}/{1} {2} {3} {4}", "interactive", defaultEnv);

        private final String language;
        private final String command;
        private final String exeName;
        private final List<String> envs;

        RunConfig(String language, String command, String exeName, List<String> envs) {
            this.language = language;
            this.command = command;
            this.exeName = exeName;
            this.envs = envs;
        }

        public String getLanguage() {
            return language;
        }

        public String getCommand() {
            return command;
        }

        public String getExeName() {
            return exeName;
        }

        public List<String> getEnvs() {
            return envs;
        }

        public static RunConfig getRunConfigByLanguage(String language) {
            for (RunConfig runConfig : RunConfig.values()) {
                if (runConfig.getLanguage().equals(language)) {
                    return runConfig;
                }
            }
            return null;
        }

    }
}
