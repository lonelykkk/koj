package com.xiao.xoj.manager.judge;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xiao.xoj.common.exception.CompileError;
import com.xiao.xoj.common.exception.SubmitError;
import com.xiao.xoj.common.exception.SystemError;
import com.xiao.xoj.pojo.dto.TestJudgeReq;
import com.xiao.xoj.pojo.dto.TestJudgeRes;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.xiao.xoj.pojo.entity.judge.JudgeCase;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.service.JudgeCaseService;
import com.xiao.xoj.service.JudgeService;
import com.xiao.xoj.utils.Constants;
import com.xiao.xoj.utils.ProblemTestCaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * @author 肖恩
 * @create 2023/5/3 21:09
 * @description: 对评测用例相关数据进行处理
 */
@Slf4j
@Component
public class JudgeStrategy {

    @Resource
    private JudgeRun judgeRun;

    @Resource
    private JudgeService judgeService;

    @Resource
    private JudgeCaseService judgeCaseService;

    @Resource
    private ProblemTestCaseUtils problemTestCaseUtils;

    public HashMap<String, Object> judge(Problem problem, Judge judge) {

        HashMap<String, Object> result = new HashMap<>();

        // 编译用户代码，并对编译过程中产生的异常进行捕获处理
        // 编译好的临时代码文件id
        String userFileId = null;
        try {
            // 编译代码
            System.out.println("===start compile===");
            Constants.CompileConfig compileConfig = Constants.CompileConfig.getCompilerByLanguage(judge.getLanguage());
            if (compileConfig != null) {
                userFileId = Compiler.compile(compileConfig, judge.getCode(), judge.getLanguage());
            }

            // 测试数据文件所在文件夹
            String testcaseDir = Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + problem.getId();

            // 将测试数据封装到JSON对象中
            JSONObject testCasesInfo = problemTestCaseUtils.loadTestCaseInfo(problem.getId(),
                    testcaseDir,
                    problem.getCaseVersion());

            // 更新评测状态为评测数据中
            UpdateWrapper<Judge> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("status", Constants.Judge.STATUS_JUDGING.getStatus())
                    .eq("submit_id", judge.getSubmitId());
            judgeService.update(judge,updateWrapper);

            // todo 获取题目数据的评测模式
//            String judgeCaseMode = testCasesInfo.getStr("mode");

            // 开始测试每个测试点
            // todo 测试
//            testcaseDir = "/judge/testcase/problem_" + problem.getId();
            System.out.println("testcaseDir = " + testcaseDir);
            List<JSONObject> allCaseResultList = judgeRun.judgeAllCase(judge.getSubmitId(),
                    problem,
                    judge.getLanguage(),
                    testcaseDir,
                    testCasesInfo,
                    userFileId,
                    judge.getCode(),
                    false,
                    "default");


            // 对全部测试点结果进行评判,获取最终评判结果
            return getJudgeInfo(allCaseResultList, problem, judge, "default");
        } catch (SystemError systemError) {
            result.put("status", Constants.Judge.STATUS_SYSTEM_ERROR.getStatus());
            result.put("errMsg", "Oops, something has gone wrong with the judgeServer. Please report this to administrator.");
            result.put("time", 0);
            result.put("memory", 0);
            log.error("[Judge] [System Error] Submit Id:[{}] Problem Id:[{}], Error:[{}]",
                    judge.getSubmitId(),
                    problem.getId(),
                    systemError);
        } catch (SubmitError submitError) {
            result.put("status", Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus());
            result.put("errMsg", mergeNonEmptyStrings(submitError.getMessage(), submitError.getStdout(), submitError.getStderr()));
            result.put("time", 0);
            result.put("memory", 0);
            log.error("[Judge] [Submit Error] Submit Id:[{}] Problem Id:[{}], Error:[{}]",
                    judge.getSubmitId(),
                    problem.getId(),
                    submitError);
        } catch (CompileError compileError) {
            result.put("status", Constants.Judge.STATUS_COMPILE_ERROR.getStatus());
            result.put("errMsg", mergeNonEmptyStrings(compileError.getStdout(), compileError.getStderr()));
            result.put("time", 0);
            result.put("memory", 0);
        } catch (Exception e) {
            result.put("status", Constants.Judge.STATUS_SYSTEM_ERROR.getStatus());
            result.put("errMsg", "Oops, something has gone wrong with the judgeServer. Please report this to administrator.");
            result.put("time", 0);
            result.put("memory", 0);
            log.error("[Judge] [System Runtime Error] Submit Id:[{}] Problem Id:[{}], Error:[{}]",
                    judge.getSubmitId(),
                    problem.getId(),
                    e);
        } finally {

            // 删除tmpfs内存中的用户代码可执行文件
            if (!StringUtils.isEmpty(userFileId)) {
                SandboxRun.delFile(userFileId);
            }
        }

        return result;
    }


    public TestJudgeRes testJudge(TestJudgeReq testJudgeReq) {
        // 代码编译文件临时Id
        String userFileId = null;
        try {
            // 对源代码进行编译
            Constants.CompileConfig compileConfig = Constants.CompileConfig.getCompilerByLanguage(testJudgeReq.getLanguage());
            if (compileConfig != null) {
                userFileId = Compiler.compile(compileConfig,
                        testJudgeReq.getCode(),
                        testJudgeReq.getLanguage());
            }
            return judgeRun.testJudgeCase(userFileId, testJudgeReq);
        }catch (SystemError systemError) {
            log.error("[Test Judge] [System Error] [{}]", systemError.toString());
            return TestJudgeRes.builder()
                    .memory(0L)
                    .time(0L)
                    .status(Constants.Judge.STATUS_COMPILE_ERROR.getStatus())
                    .stderr("Oops, something has gone wrong with the judgeServer. Please report this to administrator.")
                    .build();
        } catch (SubmitError submitError) {
            log.error("[Test Judge] [Submit Error] [{}]", submitError.toString());
            return TestJudgeRes.builder()
                    .memory(0L)
                    .time(0L)
                    .status(Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus())
                    .stderr(mergeNonEmptyStrings(submitError.getMessage(), submitError.getStdout(), submitError.getStderr()))
                    .build();
        } catch (CompileError compileError) {
            return TestJudgeRes.builder()
                    .memory(0L)
                    .time(0L)
                    .status(Constants.Judge.STATUS_COMPILE_ERROR.getStatus())
                    .stderr(mergeNonEmptyStrings(compileError.getStdout(), compileError.getStderr()))
                    .build();
        } catch (Exception e) {
            log.error("[Test Judge] [Error] [{}]", e.toString());
            return TestJudgeRes.builder()
                    .memory(0L)
                    .time(0L)
                    .status(Constants.Judge.STATUS_COMPILE_ERROR.getStatus())
                    .stderr("Oops, something has gone wrong with the judgeServer. Please report this to administrator.")
                    .build();
        } finally {
            // 删除tmpfs内存中的用户代码可执行文件
            if (!StringUtils.isEmpty(userFileId)) {
                SandboxRun.delFile(userFileId);
            }
        }

    }

    // 进行最终测试结果的判断（除编译失败外的评测状态码和时间，空间,OI题目的得分）
    public HashMap<String, Object> getJudgeInfo(List<JSONObject> allCaseResultList,
                                                Problem problem,
                                                Judge judge,
                                                String judgeCaseMode) {

        boolean isACM = problem.getType().equals(Constants.Contest.TYPE_ACM.getCode());

        List<JSONObject> errorCaseList = new ArrayList<>();

        List<JudgeCase> allCaseList = new ArrayList<>();

        // 记录所有测试点的结果
        allCaseResultList.forEach(jsonObject -> {
            Integer time = jsonObject.getLong("time").intValue();
            Integer memory = jsonObject.getLong("memory").intValue();
            Integer status = jsonObject.getInt("status");

            Long caseId = jsonObject.getLong("caseId", null);
            Integer seq = jsonObject.getInt("seq");

            String inputFileName = jsonObject.getStr("inputFileName");
            String outputFileName = jsonObject.getStr("outputFileName");
            String errMsg = jsonObject.getStr("errMsg");

            JudgeCase judgeCase = new JudgeCase();
            judgeCase.setRunTime(time)
                    .setRunMemory(memory)
                    .setStatus(status)
                    .setInputData(inputFileName)
                    .setOutput(outputFileName)
                    .setPid(problem.getId())
                    .setUserId(judge.getUserId())
                    .setCaseId(caseId.intValue())
                    .setSeq(seq)
                    .setMode(judgeCaseMode)
                    .setSubmitId(judge.getSubmitId());

            if (!StringUtils.isEmpty(errMsg)) {
                judgeCase.setUserOutput(errMsg);
            }

            if (!Objects.equals(status, Constants.Judge.STATUS_ACCEPTED.getStatus())) {
                errorCaseList.add(jsonObject);
            }

            allCaseList.add(judgeCase);
        });

        // todo 更新到数据库
        boolean addRes = judgeCaseService.saveBatch(allCaseList);
        if (!addRes) {
             // todo log记录该题评价记录记录失败
        }

        // 获取判题的运行时间，运行空间，OI得分
        HashMap<String, Object> result = computeResultInfo(allCaseList,
                isACM,
                errorCaseList,
                judgeCaseMode);

        // 如果该题为ACM类型的题目，多个测试点全部正确则AC，否则取第一个错误的测试点的状态
        // 如果该题为OI类型的题目, 若多个测试点全部正确则AC，若全部错误则取第一个错误测试点状态，否则为部分正确
        if (errorCaseList.size() == 0) {
            result.put("status", Constants.Judge.STATUS_ACCEPTED.getStatus());
        } else if (isACM || errorCaseList.size() == allCaseList.size()) {
            // ACM类型 或者 全部错误
            // 使用第一个错误用例的数据
            result.put("status", errorCaseList.get(0).getInt("status"));
            result.put("errMsg", errorCaseList.get(0).getStr("errMsg"));
        } else {
            // OI类型部分通过
            result.put("status", Constants.Judge.STATUS_PARTIAL_ACCEPTED.getStatus());
        }

        return result;
    }


    // 获取判题的运行时间，运行空间，OI得分
    private HashMap<String, Object> computeResultInfo(List<JudgeCase> allCaseResultList,
                                                      Boolean isACM,
                                                      List<JSONObject> errorCaseList,
                                                      String judgeCaseMode) {
        HashMap<String, Object> result = new HashMap<>();

        // 用时和内存占用保存为多个测试点中最长的
        allCaseResultList.stream().max(Comparator.comparing(JudgeCase::getRunTime))
                .ifPresent(t -> result.put("time", t.getRunTime()));

        allCaseResultList.stream().max(Comparator.comparing(JudgeCase::getRunMemory))
                .ifPresent(t -> result.put("memory", t.getRunMemory()));

        // OI题目计算得分
        if (!isACM) {
            int allTestCaseNum = allCaseResultList.size();
            int errorTestCaseNum = errorCaseList.size();
            int totalScore = 100; // OI题目的总分默认为 100

            if (errorTestCaseNum != 0) {
                totalScore = (int) (Math.floor(100.0 * (allTestCaseNum - errorTestCaseNum) / allTestCaseNum));
            }
            result.put("score", totalScore);
        }

        return result;
    }


    public String mergeNonEmptyStrings(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String str : strings) {
            if (!StringUtils.isEmpty(str)) {
                sb.append(str.substring(0, Math.min(1024 * 1024, str.length()))).append("\n");
            }
        }
        return sb.toString();
    }

}
