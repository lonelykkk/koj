package com.xiao.xoj.controller.front;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.vo.AnnouncementVO;
import com.xiao.xoj.pojo.vo.ContestVO;
import com.xiao.xoj.pojo.vo.SubmissionStatisticsVO;
import com.xiao.xoj.service.AnnouncementService;
import com.xiao.xoj.service.ContestService;
import com.xiao.xoj.service.JudgeService;
import com.xiao.xoj.service.front.FrontContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 肖恩
 * @create 2023/7/13 8:52
 * @description: TODO
 */
@RestController
@RequestMapping("/xoj/front-home")
public class FrontHomeController {

    @Autowired
    private FrontContestService frontContestService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private JudgeService judgeService;

    /**
     * @des: 获取最近14天的比赛信息列表
     *
     * @return
     */
    @GetMapping("get-recent-contest")
    public ResultData<List<ContestVO>> getRecentContest() {
        List<ContestVO> data = frontContestService.getRecentContest();
        return ResultData.success(data);
    }


    /**
     * @des: 获取主页公告列表
     *
     * @param limit
     * @param currentPage
     * @return
     */
    @GetMapping("get-announcement")
    public ResultData<IPage<AnnouncementVO>> getAnnouncement(@RequestParam(value = "limit", required = false) Integer limit,
                                                             @RequestParam(value = "currentPage", required = false) Integer currentPage) {

        IPage<AnnouncementVO> data = announcementService.getAnnouncementList(limit, currentPage, false);
        return ResultData.success(data);
    }


    /**
     * @des: 获取最近一周提交统计
     *
     * @return
     */
    @GetMapping("get-recent-week-submission")
    public ResultData<SubmissionStatisticsVO> getRecentWeekSubmission() {
        SubmissionStatisticsVO submissionStatisticsVO = judgeService.getRecentWeekSubmission();
        return ResultData.success(submissionStatisticsVO);
    }
}
