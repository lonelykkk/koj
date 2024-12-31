package com.xiao.xoj.controller.front;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.manager.judge.JudgeManager;
import com.xiao.xoj.pojo.dto.SubmitJudgeDTO;
import com.xiao.xoj.pojo.dto.TestJudgeDTO;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.xiao.xoj.pojo.vo.JudgeVO;
import com.xiao.xoj.pojo.vo.TestJudgeVO;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 肖恩
 * @create 2023/4/18 11:13
 * @description: TODO
 */
@RestController
@RequestMapping("/xoj/front-judge")
public class FrontJudgeController {

    @Autowired
    private JudgeManager judgeManager;

    /**
     * @des: 提交代码，进行评测
     *
     * @param judgeDTO
     * @return
     */
    @PostMapping("submit-problem-judge")
    @RequiresAuthentication
    public ResultData<Judge> submitProblemJudge(@RequestBody SubmitJudgeDTO judgeDTO) {
        Judge judge = judgeManager.submitProblemJudge(judgeDTO);
        return ResultData.success(judge);
    }


    /**
     * @des: 提交代码，进行测试用例评测
     *
     * @param testJudgeDTO
     * @return
     */
    @PostMapping("submit-problem-test-judge")
    @RequiresAuthentication
    public ResultData<String> submitProblemTestJudge(@RequestBody TestJudgeDTO testJudgeDTO) {
        String uniqueKey = judgeManager.submitProblemTestJudge(testJudgeDTO);
        return  ResultData.success(uniqueKey, null);
    }

    /**
     * @des： 对提交列表状态为Pending和Judging的提交进行更新检查
     *
     * @param submitId
     * @return
     */
    @PostMapping("check-submission-status")
    @RequiresAuthentication
    public ResultData<Judge> checkSubmissionStatus(@RequestParam(value = "submitId") Integer submitId) {
        Judge result = judgeManager.checkSubmissionStatus(submitId);
        return ResultData.success(result);
    }


    /**
     * @des: 获取测试用例的评测结果
     *
     * @param testJudgeKey
     * @return
     */
    @PostMapping("get-test-judge-result")
    @RequiresAuthentication
    public ResultData<TestJudgeVO> getTestJudgeResult(@RequestParam(value = "testJudgeKey") String testJudgeKey) {
        TestJudgeVO testJudgeVO = judgeManager.getTestJudgeResult(testJudgeKey);
        return ResultData.success(testJudgeVO);
    }



    /**
     * @des: 获取该题的的提交记录
     *
     * @param limit
     * @param currentPage
     * @param onlyMine
     * @param problemID
     * @return
     */
    @GetMapping("get-submission-list")
    @RequiresAuthentication
    public ResultData<IPage<JudgeVO>> getSubmissionList(@RequestParam(value = "limit", required = false) Integer limit,
                                                        @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                        @RequestParam(value = "onlyMine", required = false) Boolean onlyMine,
                                                        @RequestParam(value = "problemID", required = true) Integer problemID) {
        Page<JudgeVO> page = judgeManager.getSubmissionList(limit, currentPage, onlyMine, problemID);
        return ResultData.success(page);
    }


    /**
     * @des: 获取单个提交记录详情
     *
     * @param submitId
     * @return
     */
    @GetMapping("get-submission-detail")
    @RequiresAuthentication
    public ResultData<Judge> getSubmissionDetail(@RequestParam(value = "submitId", required = true) Integer submitId) {
        Judge judge = judgeManager.getSubmissionDetail(submitId);
        return ResultData.success(judge);
    }


    // todo 下载测试用例文件

}
