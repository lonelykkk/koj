package com.xiao.xoj.manager.judge;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.common.exception.StatusForbiddenException;
import com.xiao.xoj.common.exception.StatusNotFoundException;
import com.xiao.xoj.pojo.dto.*;
import com.xiao.xoj.pojo.entity.contest.Contest;
import com.xiao.xoj.pojo.entity.contest.ContestProblem;
import com.xiao.xoj.pojo.entity.contest.ContestRecord;
import com.xiao.xoj.pojo.entity.contest.ContestRegister;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.vo.JudgeVO;
import com.xiao.xoj.pojo.vo.TestJudgeVO;
import com.xiao.xoj.service.*;
import com.xiao.xoj.shiro.AccountProfile;
import com.xiao.xoj.utils.Constants;
import com.xiao.xoj.utils.IpUtils;
import com.xiao.xoj.utils.JudgeValidatorUtil;
import com.xiao.xoj.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author 肖恩
 * @create 2023/4/27 19:47
 * @description: TODO
 */
@Slf4j(topic = "xoj")
@Component
public class JudgeManager {

    @Autowired
    private JudgeValidatorUtil judgeValidatorUtil;

    @Autowired
    private JudgeDispatcher judgeDispatcher;

    @Autowired
    private JudgeService judgeService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ContestService contestService;

    @Autowired
    private ContestRegisterService contestRegisterService;

    @Autowired
    private ContestProblemService contestProblemService;

    @Autowired
    private ContestRecordService contestRecordService;

    public Judge submitProblemJudge(SubmitJudgeDTO judgeDTO) {

        // 数据校验
        judgeValidatorUtil.validatorSubmissionInfo(judgeDTO);

        boolean isContestSubmission = judgeDTO.getCid() != null && judgeDTO.getCid() != 0;

        // 获取用户当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        // 限制提交频率
        String lockKey = Constants.Account.SUBMIT_JUDGE_LOCK.getCode() + accountProfile.getId();
        long count = redisUtils.incr(lockKey, 1);
        if (count > 1) {
            long expireTime = redisUtils.getExpire(lockKey);
            throw new StatusFailException("对不起，您的提交频率过快，请" + expireTime + "秒后再尝试！");
        }
        redisUtils.expire(lockKey, 5); // 提交间隔为5秒


        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();

        // 初始化数据并写入数据库
        Judge judge = new Judge();
        judge.setCode(judgeDTO.getCode())
                .setCid(judgeDTO.getCid())
                .setCodeLength(judgeDTO.getCode().length())
                .setLanguage(judgeDTO.getLanguage())
                .setUserId(accountProfile.getUid())
                .setUsername(accountProfile.getUsername())
                .setStatus(Constants.Judge.STATUS_PENDING.getStatus())
                .setSubmitTime(new Date())
                .setIp(IpUtils.getUserIpAddr(request));

        if (isContestSubmission) {
            // 考核提交初始化
            initContestSubmission(judgeDTO.getCid(), judgeDTO.getPid(), judge);
        } else {
            // 普通提交初始化
            initCommonSubmission(judgeDTO.getPid(), judge);
        }

        // 提交评测任务进行分发
        judgeDispatcher.sendTask(judge.getSubmitId(), judge.getPid(), isContestSubmission);
        log.info("submitProblemJudge Thread:" + Thread.currentThread().getName());
        return judge;
    }


    public String submitProblemTestJudge(TestJudgeDTO testJudgeDTO) {
        // 数据校验
        judgeValidatorUtil.validatorSubmissionTestInfo(testJudgeDTO);

        // 获取用户当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        // 提交频率限制
        String lockKey = Constants.Account.TEST_JUDGE_LOCK.getCode() + accountProfile.getUid();
        long count = redisUtils.incr(lockKey, 1);
        if (count > 1) {
            long expireTime = redisUtils.getExpire(lockKey);
            throw new StatusForbiddenException("对不起，您使用在线调试过于频繁，请" + expireTime + "秒后再尝试！");
        }
        redisUtils.expire(lockKey, 5); // 每5秒能提交一次在线调试


        Problem problem = problemService.getById(testJudgeDTO.getPid());
        if (problem == null) {
            throw new StatusFailException("当前题目不存在！不支持在线调试！");
        }

        String uniqueKey = "TEST_JUDGE_" + IdUtil.simpleUUID();
        TestJudgeReq testJudgeReq = new TestJudgeReq();
        testJudgeReq.setMemoryLimit(problem.getMemoryLimit())
                .setTimeLimit(problem.getTimeLimit())
                .setStackLimit(problem.getStackLimit())
                .setCode(testJudgeDTO.getCode())
                .setLanguage(testJudgeDTO.getLanguage())
                .setUniqueKey(uniqueKey)
                .setExpectedOutput(testJudgeDTO.getExpectedOutput())
                .setTestCaseInput(testJudgeDTO.getUserInput())
                .setProblemJudgeMode(testJudgeDTO.getMode())
                .setIsRemoveEndBlank(problem.getIsRemoveEndBlank());

        // 将评测结果保存到redis中
        redisUtils.set(uniqueKey, TestJudgeRes.builder()
                .status(Constants.Judge.STATUS_PENDING.getStatus())
                .build());

        judgeDispatcher.sendTestJudgeTask(testJudgeReq);
        return uniqueKey;
    }


    /**
     * @des: 普通题目评测初始化
     *
     * @param problemId
     * @param judge
     * @return
     */
    public void initCommonSubmission(Integer problemId, Judge judge) {
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", problemId);
        Problem problem = problemService.getOne(queryWrapper);

        if (problem == null) {
            throw new StatusFailException("题目不存在！");
        }
        if (problem.getAuth() == 2) {
            throw new StatusForbiddenException("错误！该题目不可提交！");
        }

        judge.setCpid(0)
                .setPid(problem.getId())
                .setDisplayId(problem.getProblemId());

        // 保存到数据库中
        judgeService.save(judge);
    }


    /**
     * @des: 考核题目评测初始化
     *
     * @param cid
     * @param pid       contest_problem表中的id
     * @param judge
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void initContestSubmission(Integer cid, Integer pid, Judge judge) {
        // 获取用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        // 首先判断一下比赛的状态是否是正在进行，结束状态都不能提交，比赛前比赛管理员可以提交
        Contest contest = contestService.getById(cid);
        if (contest == null) {
            throw new StatusNotFoundException("对不起，该比赛不存在！");
        }
        if (contest.getStatus().intValue() == Constants.Contest.STATUS_ENDED.getCode()) {
            throw new StatusFailException("考核已结束，不可再提交！");
        }

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");
        if (!isRoot && !isAdmin) {
            if (contest.getStatus().intValue() == Constants.Contest.STATUS_SCHEDULED.getCode()) {
                throw new StatusForbiddenException("比赛未开始，不可提交！");
            }

            // 查看用户是否报名该考核
            QueryWrapper<ContestRegister> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("cid", cid).eq("user_id", accountProfile.getId());
            ContestRegister register = contestRegisterService.getOne(queryWrapper);
            if (register == null) {
                throw new StatusForbiddenException("对不起，请你先注册该比赛，提交代码失败！");
            }
        }

        // 查询对应的cpid
        ContestProblem contestProblem = contestProblemService.getById(pid);
        judge.setCpid(contestProblem.getId())
                .setPid(contestProblem.getPid())
                .setDisplayId(contestProblem.getDisplayId());

        // 将数据保存到数据库中
        judgeService.save(judge);

        // 同时初始化写入contest_record表
        ContestRecord contestRecord = new ContestRecord();
        contestRecord.setDisplayId(contestProblem.getDisplayId())
                .setCpid(contestProblem.getId())
                .setSubmitId(judge.getSubmitId())
                .setPid(judge.getPid())
                .setUsername(accountProfile.getUsername())
                .setRealname(accountProfile.getRealname())
                .setUserId(accountProfile.getUid())
                .setCid(judge.getCid())
                .setSubmitTime(judge.getSubmitTime());

        if (contest.getStatus().intValue() == Constants.Contest.STATUS_SCHEDULED.getCode()) {
            contestRecord.setTime(0);
        } else {
            // 设置比赛开始时间到提交时间之间的秒数
            contestRecord.setTime((int)DateUtil.between(contest.getStartTime(), judge.getSubmitTime(), DateUnit.SECOND));
        }
        contestRecordService.save(contestRecord);
    }


    // 查询题目评测状态
    public Judge checkSubmissionStatus(Integer submitId) {
        if (submitId == null) {
            return null;
        }
        return judgeService.getById(submitId);
    }


    // 查询测试用例的评测状态：PENDING, ACCEPTED, SYSTEM_ERROR
    public TestJudgeVO getTestJudgeResult(String testJudgeKey) {
        TestJudgeRes testJudgeRes = (TestJudgeRes) redisUtils.get(testJudgeKey);
        if (testJudgeRes == null) {
            throw new StatusFailException("查询错误！当前在线调试任务不存在！");
        }

        TestJudgeVO testJudgeVO = new TestJudgeVO();
        testJudgeVO.setStatus(testJudgeRes.getStatus());
        if (testJudgeVO.getStatus().equals(Constants.Judge.STATUS_PENDING.getStatus())) {
            return testJudgeVO;
        }
        testJudgeVO.setTime(testJudgeRes.getTime());
        testJudgeVO.setMemory(testJudgeRes.getMemory());
        testJudgeVO.setUserInput(testJudgeRes.getInput());
        testJudgeVO.setUserOutput(testJudgeRes.getStdout());
        testJudgeVO.setExpectedOutput(testJudgeRes.getExpectedOutput());
        testJudgeVO.setStderr(testJudgeRes.getStderr());
        testJudgeVO.setProblemJudgeMode(testJudgeRes.getProblemJudgeMode());

        // 从redis中删除该评测数据
        redisUtils.del(testJudgeKey);
        return testJudgeVO;
    }


    // 获取普通题目的提交列表
    public Page<JudgeVO> getSubmissionList(Integer limit, Integer currentPage, Boolean onlyMine, Integer problemID) {
        if (limit == null || limit < 1) limit = 20;
        if (currentPage == null || currentPage < 1) currentPage = 1;

        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        String userId = null;
        if (onlyMine) {
            if (accountProfile == null) {
                throw new StatusFailException("当前用户数据为空，请您重新登陆！");
            }
            userId = accountProfile.getUid();
        }

        return judgeService.getSubmissionList(limit, currentPage, userId, problemID);
    }


    /**
     * @des: 获取单个提交记录的详情
     *
     * @param submitId
     * @return
     */
    public Judge getSubmissionDetail(Integer submitId) {
        // 获取用户当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        Judge judge = judgeService.getById(submitId);
        if (judge == null) {
            throw new StatusFailException("此提交数据不存在！");
        }

        // 考核期间，非本人无法查看提交的详细记录，管理员除外
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");
        if (judge.getCid() != 0) {
            Contest contest = contestService.getById(judge.getCid());
            if (!isRoot && !isAdmin) {
                // 不是本人的话不能查看时间，空间，长度，错误提示信息
                if (!judge.getUserId().equals(accountProfile.getId())) {
                    judge.setRunTime(null);
                    judge.setRunMemory(null);
                    judge.setCodeLength(null);
                    judge.setErrorMessage(null);
                    // 如果还在考核期间，不是本人不能查看代码
                    if (contest.getStatus().intValue() == Constants.Contest.STATUS_RUNNING.getCode()) {
                        judge.setCode(null);
                    }
                }
            }
        } else {
            // 不是本人的话不能查看时间，空间，长度，错误提示信息
            if (!judge.getUserId().equals(accountProfile.getId())) {
                judge.setRunTime(null);
                judge.setRunMemory(null);
                judge.setCodeLength(null);
                judge.setErrorMessage(null);
            }
        }

        return judge;
    }
}
