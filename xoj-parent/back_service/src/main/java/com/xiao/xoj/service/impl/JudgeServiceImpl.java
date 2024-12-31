package com.xiao.xoj.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.pojo.entity.contest.ContestRecord;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.xiao.xoj.mapper.JudgeMapper;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.vo.JudgeVO;
import com.xiao.xoj.pojo.vo.SubmissionStatisticsVO;
import com.xiao.xoj.service.ContestRecordService;
import com.xiao.xoj.service.JudgeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.service.ProblemService;
import com.xiao.xoj.utils.Constants;
import com.xiao.xoj.utils.RedisUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-04-27
 */
@Service
public class JudgeServiceImpl extends ServiceImpl<JudgeMapper, Judge> implements JudgeService {

    @Resource
    private JudgeMapper judgeMapper;

    @Resource
    private ProblemService problemService;

    @Autowired
    private ContestRecordService contestRecordService;

    @Autowired
    private RedisUtils redisUtils;

    private final static String SUBMISSION_STATISTICS_KEY = "home_submission_statistics";

    @Override
    public Page<JudgeVO> getSubmissionList(Integer limit, Integer currentPage, String userId, Integer problemID) {
        Page<JudgeVO> page = new Page<>(currentPage, limit);
        Page<JudgeVO> submissionList = judgeMapper.getSubmissionList(page, userId, problemID);
        List<JudgeVO> records = submissionList.getRecords();
        // 设置标题
        if (!CollectionUtils.isEmpty(records)) {
            Problem problem = problemService.getById(problemID);
            for (JudgeVO judgeVO : records)
                judgeVO.setTitle(problem.getTitle());
        }
        return submissionList;
    }


    @Override
    public IPage<JudgeVO> getContestSubmissionList(Integer limit,
                                                   Integer currentPage,
                                                   Integer cid,
                                                   String userId) {

        Page<JudgeVO> page = new Page<>(currentPage, limit);
        return judgeMapper.getContestSubmissionList(page, cid, userId);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void failToUseRedisPublishJudge(Integer submitId, Integer pid, Boolean isContest) {
        UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
        judgeUpdateWrapper.eq("submit_id", submitId)
                .set("error_message", "系统内部错误，请联系管理员处理！")
                .set("status", Constants.Judge.STATUS_SYSTEM_ERROR.getStatus());
        judgeMapper.update(null, judgeUpdateWrapper);
        // 更新contest_record表
        if (isContest) {
            UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("submit_id", submitId) // submit_id一定只有一个
                    .set("status", Constants.Contest.RECORD_NOT_AC_NOT_PENALTY.getCode()); // 系统错误，不罚时
            contestRecordService.update(null ,updateWrapper);
        }
    }


    @Override
    public SubmissionStatisticsVO getRecentWeekSubmission() {
        SubmissionStatisticsVO submissionStatisticsVO = (SubmissionStatisticsVO) redisUtils.get(SUBMISSION_STATISTICS_KEY);

        if (submissionStatisticsVO == null ) {
            DateTime dateTime = DateUtil.offsetDay(new Date(), -6);
            String strTime = DateFormatUtils.format(dateTime, "yyyy-MM-dd") + " 00:00:00";
            QueryWrapper<Judge> judgeQueryWrapper = new QueryWrapper<>();
            judgeQueryWrapper.select("submit_id", "status", "gmt_create");
            judgeQueryWrapper.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + strTime + "')");
            List<Judge> judgeList = this.list(judgeQueryWrapper);
            submissionStatisticsVO = buildSubmissionStatisticsVo(judgeList);
            redisUtils.set(SUBMISSION_STATISTICS_KEY, submissionStatisticsVO, 60 * 30);
        }
        return submissionStatisticsVO;
    }

    private SubmissionStatisticsVO buildSubmissionStatisticsVo(List<Judge> judgeList) {
        long acTodayCount = 0;
        long acOneDayAgoCount = 0;
        long acTwoDaysAgoCount = 0;
        long acThreeDaysAgoCount = 0;
        long acFourDaysAgoCount = 0;
        long acFiveDaysAgoCount = 0;
        long acSixDaysAgoCount = 0;

        long totalTodayCount = 0;
        long totalOneDayAgoCount = 0;
        long totalTwoDaysAgoCount = 0;
        long totalThreeDaysAgoCount = 0;
        long totalFourDaysAgoCount = 0;
        long totalFiveDaysAgoCount = 0;
        long totalSixDaysAgoCount = 0;


        Date date = new Date();
        String todayStr = DateUtil.format(date, "MM-dd");
        String oneDayAgoStr = DateFormatUtils.format(DateUtil.offsetDay(date, -1), "MM-dd");
        String twoDaysAgoStr = DateFormatUtils.format(DateUtil.offsetDay(date, -2), "MM-dd");
        String threeDaysAgoStr = DateFormatUtils.format(DateUtil.offsetDay(date, -3), "MM-dd");
        String fourDaysAgoStr = DateFormatUtils.format(DateUtil.offsetDay(date, -4), "MM-dd");
        String fiveDaysAgoStr = DateFormatUtils.format(DateUtil.offsetDay(date, -5), "MM-dd");
        String sixDaysAgoStr = DateFormatUtils.format(DateUtil.offsetDay(date, -6), "MM-dd");

        if (!CollectionUtils.isEmpty(judgeList)) {
            Map<String, List<Judge>> map = judgeList.stream()
                    .collect(
                            Collectors.groupingBy(
                                    o -> DateUtil.format(o.getGmtCreate(), "MM-dd"))
                    );
            for (Map.Entry<String, List<Judge>> entry : map.entrySet()) {
                if (Objects.equals(entry.getKey(), todayStr)) {
                    totalTodayCount += entry.getValue().size();
                    long count = entry.getValue()
                            .parallelStream()
                            .filter(judge -> Objects.equals(judge.getStatus(), Constants.Judge.STATUS_ACCEPTED.getStatus()))
                            .count();
                    acTodayCount += count;
                } else if (Objects.equals(entry.getKey(), oneDayAgoStr)) {
                    totalOneDayAgoCount += entry.getValue().size();
                    long count = entry.getValue()
                            .parallelStream()
                            .filter(judge -> Objects.equals(judge.getStatus(), Constants.Judge.STATUS_ACCEPTED.getStatus()))
                            .count();
                    acOneDayAgoCount += count;
                } else if (Objects.equals(entry.getKey(), twoDaysAgoStr)) {
                    totalTwoDaysAgoCount += entry.getValue().size();
                    long count = entry.getValue()
                            .parallelStream()
                            .filter(judge -> Objects.equals(judge.getStatus(), Constants.Judge.STATUS_ACCEPTED.getStatus()))
                            .count();
                    acTwoDaysAgoCount += count;
                } else if (Objects.equals(entry.getKey(), threeDaysAgoStr)) {
                    totalThreeDaysAgoCount += entry.getValue().size();
                    long count = entry.getValue()
                            .parallelStream()
                            .filter(judge -> Objects.equals(judge.getStatus(), Constants.Judge.STATUS_ACCEPTED.getStatus()))
                            .count();
                    acThreeDaysAgoCount += count;
                } else if (Objects.equals(entry.getKey(), fourDaysAgoStr)) {
                    totalFourDaysAgoCount += entry.getValue().size();
                    long count = entry.getValue()
                            .parallelStream()
                            .filter(judge -> Objects.equals(judge.getStatus(), Constants.Judge.STATUS_ACCEPTED.getStatus()))
                            .count();
                    acFourDaysAgoCount += count;
                } else if (Objects.equals(entry.getKey(), fiveDaysAgoStr)) {
                    totalFiveDaysAgoCount += entry.getValue().size();
                    long count = entry.getValue()
                            .parallelStream()
                            .filter(judge -> Objects.equals(judge.getStatus(), Constants.Judge.STATUS_ACCEPTED.getStatus()))
                            .count();
                    acFiveDaysAgoCount += count;
                } else if (Objects.equals(entry.getKey(), sixDaysAgoStr)) {
                    totalSixDaysAgoCount += entry.getValue().size();
                    long count = entry.getValue()
                            .parallelStream()
                            .filter(judge -> Objects.equals(judge.getStatus(), Constants.Judge.STATUS_ACCEPTED.getStatus()))
                            .count();
                    acSixDaysAgoCount += count;
                }
            }
        }

        SubmissionStatisticsVO submissionStatisticsVO = new SubmissionStatisticsVO();
        submissionStatisticsVO.setDateStrList(Arrays.asList(
                sixDaysAgoStr,
                fiveDaysAgoStr,
                fourDaysAgoStr,
                threeDaysAgoStr,
                twoDaysAgoStr,
                oneDayAgoStr,
                todayStr));

        submissionStatisticsVO.setAcCountList(Arrays.asList(
                acSixDaysAgoCount,
                acFiveDaysAgoCount,
                acFourDaysAgoCount,
                acThreeDaysAgoCount,
                acTwoDaysAgoCount,
                acOneDayAgoCount,
                acTodayCount));

        submissionStatisticsVO.setTotalCountList(Arrays.asList(
                totalSixDaysAgoCount,
                totalFiveDaysAgoCount,
                totalFourDaysAgoCount,
                totalThreeDaysAgoCount,
                totalTwoDaysAgoCount,
                totalOneDayAgoCount,
                totalTodayCount));

        return submissionStatisticsVO;
    }
}
