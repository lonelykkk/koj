package com.xiao.xoj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.vo.JudgeVO;
import com.xiao.xoj.pojo.vo.SubmissionStatisticsVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-04-27
 */
public interface JudgeService extends IService<Judge> {

    Page<JudgeVO> getSubmissionList(Integer limit, Integer currantPage, String userId, Integer problemID);

    IPage<JudgeVO> getContestSubmissionList(Integer limit, Integer currentPage, Integer cid, String userId);

    void failToUseRedisPublishJudge(Integer judgeId, Integer pid, Boolean isContest);

    SubmissionStatisticsVO getRecentWeekSubmission();
}
