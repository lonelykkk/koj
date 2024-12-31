package com.xiao.xoj.controller;

import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.common.result.ResultStatus;
import com.xiao.xoj.pojo.dto.TestJudgeReq;
import com.xiao.xoj.pojo.dto.TestJudgeRes;
import com.xiao.xoj.pojo.dto.ToJudgeDTO;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.xiao.xoj.service.JudgeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 肖恩
 * @create 2023/5/3 16:38
 * @description: 处理代码提交
 */
@RestController
public class JudgeController {

    @Value("${judge-token}")
    private String judgeToken;

    @Resource
    private JudgeService judgeService;

    @PostMapping("judge")
    public ResultData<Void> submitProblemJudge(@RequestBody ToJudgeDTO toJudgeDTO) {
        if (!toJudgeDTO.getToken().equals(judgeToken)) {
            return ResultData.fail(ResultStatus.ACCESS_DENIED.getStatus(), "对不起！您使用的判题服务调用凭证不正确！访问受限！");
        }

        Judge judge = toJudgeDTO.getJudge();
        if (judge == null || judge.getSubmitId() == null || judge.getUserId() == null || judge.getPid() == null) {
            return ResultData.fail("调用参数错误！请检查您的调用参数！");
        }

        judgeService.judge(judge);

        return ResultData.success("判题机评测完成!");
    }


    @PostMapping(value = "/test-judge")
    public ResultData<TestJudgeRes> submitProblemTestJudge(@RequestBody TestJudgeReq testJudgeReq) {

        if (testJudgeReq == null
                || StringUtils.isEmpty(testJudgeReq.getCode())
                || StringUtils.isEmpty(testJudgeReq.getLanguage())
                || StringUtils.isEmpty(testJudgeReq.getUniqueKey())
                || testJudgeReq.getTimeLimit() == null
                || testJudgeReq.getMemoryLimit() == null
                || testJudgeReq.getStackLimit() == null
                ) {
            return ResultData.fail("调用参数错误！请检查您的调用参数！");
        }

        if (!testJudgeReq.getToken().equals(judgeToken)) {
            return ResultData.fail(ResultStatus.ACCESS_DENIED.getStatus(), "对不起！您使用的判题服务调用凭证不正确！访问受限！");
        }

        TestJudgeRes testJudgeRes = judgeService.testJudge(testJudgeReq);
        return ResultData.success(testJudgeRes);
    }

}
