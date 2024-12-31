package com.xiao.xoj.manager.judge;

import com.xiao.xoj.pojo.dto.TestJudgeReq;
import com.xiao.xoj.pojo.dto.TestJudgeRes;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.service.ContestRecordService;
import com.xiao.xoj.utils.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author 肖恩
 * @create 2023/5/3 21:09
 * @description: 对评测数据进行处理
 */
@Component
public class JudgeContext {

    @Resource
    private JudgeStrategy judgeStrategy;

    @Resource
    private ContestRecordService contestRecordService;

    public Judge judge(Problem problem, Judge judge) {
        // 设置评测前的相关数据
        // c和c++为一倍时间和空间，其它语言为2倍时间和空间
        if (!judge.getLanguage().equals("C++")
                && !judge.getLanguage().equals("C")
                && !judge.getLanguage().equals("C++ With O2")
                && !judge.getLanguage().equals("C With O2")) {
            problem.setTimeLimit(problem.getTimeLimit() * 2);
            problem.setMemoryLimit(problem.getMemoryLimit() * 2);
        }

        // 评测
        System.out.println("===start judge===");
        HashMap<String, Object> judgeResult = judgeStrategy.judge(problem, judge);

        // 封装返回的评测结果
        Judge finalJudgeRes = new Judge();
        finalJudgeRes.setSubmitId(judge.getSubmitId());
        // 如果是编译失败，运行错误，系统错误或者提交错误需要有错误提醒
        if (judgeResult.get("status") == Constants.Judge.STATUS_COMPILE_ERROR.getStatus() ||
                judgeResult.get("status") == Constants.Judge.STATUS_RUNTIME_ERROR.getStatus() ||
                judgeResult.get("status") == Constants.Judge.STATUS_SYSTEM_ERROR.getStatus() ||
                judgeResult.get("status") == Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus() ) {
            finalJudgeRes.setErrorMessage((String) judgeResult.getOrDefault("errMsg", ""));
        }
        // 设置最终结果状态码
        finalJudgeRes.setStatus((Integer) judgeResult.get("status"));
        // 设置最大时间和最大空间不超过题目限制时间和空间
        // kb
        Integer memory = (Integer) judgeResult.get("memory");
        finalJudgeRes.setRunMemory(Math.min(memory, problem.getMemoryLimit() * 1024));
        // ms
        Integer time = (Integer) judgeResult.get("time");
        finalJudgeRes.setRunTime(Math.min(time, problem.getTimeLimit()));
        // score
        finalJudgeRes.setScore((Integer) judgeResult.getOrDefault("score", null));

        return finalJudgeRes;
    }


    public TestJudgeRes testJudge(TestJudgeReq testJudgeReq) {
        // c和c++为一倍时间和空间，其它语言为2倍时间和空间
        if (!testJudgeReq.getLanguage().equals("C++")
                && !testJudgeReq.getLanguage().equals("C")
                && !testJudgeReq.getLanguage().equals("C++ With O2")
                && !testJudgeReq.getLanguage().equals("C With O2")) {
            testJudgeReq.setTimeLimit(testJudgeReq.getTimeLimit() * 2);
            testJudgeReq.setMemoryLimit(testJudgeReq.getMemoryLimit() * 2);
        }

        return judgeStrategy.testJudge(testJudgeReq);
    }


    /**
     * @des: 评测完成后，更新相关表的数据
     *
     * @param submitId
     * @param status
     * @param cid
     * @param userId
     * @param pid
     * @param score
     * @param runTime
     * @return
     */
    public void updateOtherTable(Integer submitId, Integer status, Integer cid, String userId, Integer pid, Integer score, Integer runTime) {
        System.out.println("更新其他表的数据");
        if (cid == 0) { // 非考核提交

        } else { // 考核提交
//            contestRecordService.updateContestRecord(score, status, submitId, runTime);
        }
    }


}
