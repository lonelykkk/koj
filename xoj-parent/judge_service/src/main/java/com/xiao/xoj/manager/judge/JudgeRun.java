package com.xiao.xoj.manager.judge;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.xiao.xoj.common.exception.SystemError;
import com.xiao.xoj.manager.judge.entity.JudgeDTO;
import com.xiao.xoj.manager.judge.entity.JudgeGlobalDTO;
import com.xiao.xoj.manager.judge.task.DefaultJudge;
import com.xiao.xoj.manager.judge.task.TestJudge;
import com.xiao.xoj.pojo.dto.TestJudgeReq;
import com.xiao.xoj.pojo.dto.TestJudgeRes;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.utils.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author 肖恩
 * @create 2023/5/3 21:10
 * @description: 该类负责输入数据进入程序进行测评
 */
@Component
public class JudgeRun {

    @Resource
    private DefaultJudge defaultJudge;

    @Resource
    private TestJudge testJudge;

    public List<JSONObject> judgeAllCase(Integer submitId,
                                         Problem problem,
                                         String judgeLanguage,
                                         String testCasesDir,
                                         JSONObject testCasesInfo,
                                         String userFileId,
                                         String userFileContent,
                                         Boolean getUserOutput,
                                         String judgeCaseMode)
            throws SystemError, ExecutionException, InterruptedException {

        if (testCasesInfo == null) {
            throw new SystemError("The evaluation data of the problem does not exist", null, null);
        }

        JSONArray testCasesList = (JSONArray) testCasesInfo.get("testCases");

        // 默认给题目限制时间+200ms用来测评
        Long testTime = (long) problem.getTimeLimit() + 200;

        // 获取评测类型，全默认为default todo 可能需要修改
        Constants.JudgeMode judgeMode = Constants.JudgeMode.getJudgeMode(judgeCaseMode);
        if (judgeMode == null) {
            throw new RuntimeException("The judge mode of problem " + problem.getProblemId() + " error:" + judgeCaseMode);
        }

        // 用户输出的文件夹
        String runDir = Constants.JudgeDir.RUN_WORKPLACE_DIR.getContent() + File.separator + submitId;
        // todo 测试
//        runDir = Constants.JudgeDir.RUN_WORKPLACE_DIR.getContent() + "/" + submitId;

        // 运行时相关环境配置变量
        Constants.RunConfig runConfig = Constants.RunConfig.getRunConfigByLanguage(judgeLanguage);

        // 评测相关配置数据封装
        JudgeGlobalDTO judgeGlobalDTO = JudgeGlobalDTO.builder()
                .problemId(problem.getId())
                .judgeMode(judgeMode)
                .userFileId(userFileId)
                .userFileContent(userFileContent)
                .runDir(runDir)
                .runConfig(runConfig)
                .testTime(testTime)
                .maxTime((long) problem.getTimeLimit())
                .maxMemory((long) problem.getMemoryLimit())
                .maxStack(problem.getStackLimit())
                .testCaseInfo(testCasesInfo)
                .needUserOutputFile(getUserOutput)
                .removeEOLBlank(problem.getIsRemoveEndBlank())
                .build();


        return defaultJudgeAllCase(testCasesList, testCasesDir, judgeGlobalDTO);

    }



    /**
     * @des 默认评测，会评测所有评测用例
     *
     * @param testCasesList
     * @param testCasesDir
     * @param judgeGlobalDTO
     * @return
     */
    private List<JSONObject> defaultJudgeAllCase(JSONArray testCasesList, String testCasesDir, JudgeGlobalDTO judgeGlobalDTO) {
        List<JSONObject> judgeResultList = new ArrayList<>();
        for (int index = 0; index < testCasesList.size(); index++) {
            JSONObject testcase = (JSONObject) testCasesList.get(index);
            // 将每个需要测试的线程任务加入任务列表中
            int testCaseId = index + 1;
            // 输入文件名
            String inputFileName = testcase.getStr("inputName");
            // 输出文件名
            String outputFileName = testcase.getStr("outputName");
            // 题目数据的输入文件的路径
            // todo test
            String inputFilePath = testCasesDir + File.separator + inputFileName;
//            String inputFilePath = testCasesDir +  "/"  + inputFileName;
            // 题目数据的输出文件的路径
            // todo test
            String outputFilePath = testCasesDir + File.separator + outputFileName;
//            String outputFilePath = testCasesDir + "/" + outputFileName;
            // 数据库表的测试样例id
            Long caseId = testcase.getLong("caseId");
            // 该测试点的满分
            Integer score = testcase.getInt("score");

            Long maxOutputSize = Math.max(testcase.getLong("outputSize", 0L) * 2, 32 * 1024 * 1024L);

            JudgeDTO judgeDTO = JudgeDTO.builder()
                    .testCaseNum(testCaseId)
                    .testCaseInputFileName(inputFileName)
                    .testCaseInputFilePath(inputFilePath)
                    .testCaseOutputFileName(outputFileName)
                    .testCaseOutputFilePath(outputFilePath)
                    .maxOutputSize(maxOutputSize)
                    .score(score)
                    .build();

            System.out.println("评测第 " + (index + 1) + " 个用例");
            // 评测用例
            JSONObject judgeRes = defaultJudge.judgeCase(judgeDTO, judgeGlobalDTO);
            judgeRes.set("caseId", caseId);
            judgeRes.set("score", judgeDTO.getScore());
            judgeRes.set("inputFileName", judgeDTO.getTestCaseInputFileName());
            judgeRes.set("outputFileName", judgeDTO.getTestCaseOutputFileName());
            judgeRes.set("seq", judgeDTO.getTestCaseNum());
            judgeResultList.add(judgeRes);

            // 判断该用例是否通过
            Integer status = judgeRes.getInt("status");
            if (!Constants.Judge.STATUS_ACCEPTED.getStatus().equals(status)) {
                System.out.println("第 " + (index + 1) + " 个用例未通过");
            }
            else System.out.println("通过第 " + (index + 1) + " 个用例");
        }

        return judgeResultList;
    }


    public TestJudgeRes testJudgeCase(String userFileId, TestJudgeReq testJudgeReq) {
        // 默认给限制时间+200ms用来测评
        Long testTime = testJudgeReq.getTimeLimit() + 200L;

        Constants.RunConfig runConfig = Constants.RunConfig.getRunConfigByLanguage(testJudgeReq.getLanguage());

        JudgeGlobalDTO judgeGlobalDTO = JudgeGlobalDTO.builder()
                .judgeMode(Constants.JudgeMode.TEST)
                .userFileId(userFileId)
                .userFileContent(testJudgeReq.getCode())
                .testTime(testTime)
                .maxMemory((long) testJudgeReq.getMemoryLimit())
                .maxTime((long) testJudgeReq.getTimeLimit())
                .maxStack( testJudgeReq.getStackLimit())
                .removeEOLBlank(testJudgeReq.getIsRemoveEndBlank())
                .runConfig(runConfig)
                .build();

        Long maxOutputSize = Math.max(testJudgeReq.getTestCaseInput().length() * 2L, 32 * 1024 * 1024L);

        JudgeDTO judgeDTO = JudgeDTO.builder()
                .testCaseInputFileContent(testJudgeReq.getTestCaseInput())
                .maxOutputSize(maxOutputSize)
                .testCaseOutputContent(testJudgeReq.getExpectedOutput())
                .build();

        JSONObject judgeRes = testJudge.judgeCase(judgeDTO, judgeGlobalDTO);

        return TestJudgeRes.builder()
                .status(judgeRes.getInt("status"))
                .memory(judgeRes.getLong("memory"))
                .time(judgeRes.getLong("time"))
                .stdout(judgeRes.getStr("output"))
                .stderr(judgeRes.getStr("errMsg"))
                .build();
    }
}
