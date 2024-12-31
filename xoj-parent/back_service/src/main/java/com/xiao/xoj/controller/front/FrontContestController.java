package com.xiao.xoj.controller.front;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.dto.RegisterContestDTO;
import com.xiao.xoj.pojo.vo.*;
import com.xiao.xoj.service.front.FrontContestService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/6/4 18:47
 * @description: TODO
 */
@RestController
@RequestMapping("/xoj/front-contest")
public class FrontContestController {

    @Resource
    private FrontContestService frontContestService;


    /**
     * @des: 获取考核列表分页数据
     *
     * @param limit
     * @param currentPage
     * @param keyword
     * @return
     */
    @GetMapping("/get-contest-list")
    public ResultData<IPage<ContestVO>> getContestList(@RequestParam(value = "limit", required = false) Integer limit,
                                                       @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                       @RequestParam(value = "keyword", required = false) String keyword) {

        IPage<ContestVO> res = frontContestService.getContestList(limit, currentPage, keyword);
        return ResultData.success(res);
    }


    /**
     * @des: 获得指定考核的详细信息
     *
     * @param cid
     * @return
     */
    @GetMapping("/get-contest-info")
    @RequiresAuthentication
    public ResultData<HashMap<String, Object>> getContestInfo(@RequestParam(value = "cid") Integer cid) {
        HashMap<String, Object> resMap = frontContestService.getContestInfo(cid);
        return ResultData.success(resMap);
    }


    /**
     * @des: 注册考核
     *
     * @param registerContestDTO
     * @return
     */
    @PostMapping("/register-contest")
    @RequiresAuthentication
    public ResultData<Void> registerContest(@Valid @RequestBody RegisterContestDTO registerContestDTO) {

        boolean isOk = frontContestService.registerContest(registerContestDTO);
        if (!isOk) {
            return ResultData.fail("注册失败！请重新尝试！");
        }
        return ResultData.success("注册成功！");
    }


    /**
     * @des: 获得指定考核的题目列表
     *
     * @param cid
     * @return
     */
    @GetMapping("/get-contest-problem")
    @RequiresAuthentication
    public ResultData<List<ContestProblemVO>> getContestProblem(@RequestParam(value = "cid", required = true) Integer cid) {

        List<ContestProblemVO> contestProblems = frontContestService.getContestProblem(cid);
        return ResultData.success(contestProblems);
    }


    /**
     * @des: 获得指定考核的题目详情
     *
     * @param cid
     * @param cpid
     * @return
     */
    @GetMapping("/get-contest-problem-details")
    @RequiresAuthentication
    public ResultData<ProblemInfoVO> getContestProblemDetails(@RequestParam(value = "cid", required = true) Integer cid,
                                                                @RequestParam(value = "cpid", required = true) Integer cpid) {

        ProblemInfoVO problemInfoVO = frontContestService.getContestProblemDetails(cid, cpid);
        return ResultData.success(problemInfoVO);
    }


    /**
     * @des: 获得指定考核题目的所有用户的提交列表
     *
     * @param limit
     * @param currentPage
     * @param onlyMine
     * @param cid
     * @return
     */
    @GetMapping("/contest-submissions")
    @RequiresAuthentication
    public ResultData<IPage<JudgeVO>> getContestSubmissionList(@RequestParam(value = "limit", required = false) Integer limit,
                                                               @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                               @RequestParam(value = "onlyMine", required = false) Boolean onlyMine,
                                                               @RequestParam(value = "contestID", required = true) Integer cid) {

        IPage<JudgeVO> judgeVOIPage = frontContestService.getContestSubmissionList(limit, currentPage, onlyMine, cid);
        return ResultData.success(judgeVOIPage);
    }


    /**
     * @des: 考核期间，获取用户考核题目的提交列表
     *
     * @param limit
     * @param currentPage
     * @return
     */
    @GetMapping("/contest-user-submissions")
    @RequiresAuthentication
    public ResultData <IPage<JudgeVO>> getContestUserSubmissions(@RequestParam(value = "limit", required = false) Integer limit,
                                                                 @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                 @RequestParam(value = "contestID", required = true) Integer cid) {
        IPage<JudgeVO> judgeVOIPage = frontContestService.getContestUserSubmission(limit, currentPage, cid);
        return ResultData.success(judgeVOIPage);
    }


    /**
     * @des: 获得指定考核的通知列表
     *
     * @param cid
     * @param limit
     * @param currentPage
     * @return
     */
    @GetMapping("/get-contest-announcement")
    @RequiresAuthentication
    public ResultData<IPage<AnnouncementVO>> getContestAnnouncement(@RequestParam(value = "cid", required = true) Integer cid,
                                                                    @RequestParam(value = "limit", required = false) Integer limit,
                                                                    @RequestParam(value = "currentPage", required = false) Integer currentPage) {

        IPage<AnnouncementVO> announcementVOIPage = frontContestService.getContestAnnouncement(limit, currentPage, cid);
        return ResultData.success(announcementVOIPage);
    }

}
