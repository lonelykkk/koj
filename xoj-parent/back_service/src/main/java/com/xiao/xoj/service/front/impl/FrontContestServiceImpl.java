package com.xiao.xoj.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.mapper.AnnouncementMapper;
import com.xiao.xoj.mapper.ContestMapper;
import com.xiao.xoj.pojo.dto.RegisterContestDTO;
import com.xiao.xoj.pojo.entity.contest.Contest;
import com.xiao.xoj.pojo.entity.contest.ContestProblem;
import com.xiao.xoj.pojo.entity.contest.ContestRegister;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.xiao.xoj.pojo.vo.*;
import com.xiao.xoj.service.*;
import com.xiao.xoj.service.front.FrontContestService;
import com.xiao.xoj.service.front.FrontProblemService;
import com.xiao.xoj.shiro.AccountProfile;
import com.xiao.xoj.utils.Constants;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 肖恩
 * @create 2023/6/4 18:51
 * @description: TODO
 */
@Service
public class FrontContestServiceImpl implements FrontContestService {

    @Resource
    private ContestMapper contestMapper;

    @Resource
    private ContestService contestService;

    @Resource
    private ContestRegisterService contestRegisterService;

    @Resource
    private ContestProblemService contestProblemService;

    @Resource
    private AnnouncementMapper announcementMapper;

    @Resource
    private ProblemService problemService;

    @Resource
    private FrontProblemService frontProblemService;

    @Resource
    private JudgeService judgeService;

    @Override
    public IPage<ContestVO> getContestList(Integer limit, Integer currentPage, String keyword) {
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        IPage<ContestVO> page = new Page<>(currentPage, limit);

        List<ContestVO> contestList = contestMapper.getContestList(page, keyword);
        setRegisterCount(contestList);

        return page.setRecords(contestList);
    }


    @Override
    public HashMap<String, Object> getContestInfo(Integer cid) {

        ContestVO contestVO = contestMapper.getContestInfoById(cid);
        if (contestVO == null) {
            throw new StatusFailException("抱歉，该考核不存在！");
        }

        // 设置当前服务器系统时间
        contestVO.setNow(new Date());

        // 设置报名人数
        int count = contestRegisterService.count(new QueryWrapper<ContestRegister>().eq("cid", cid));
        contestVO.setCount(count);

        // 获取用户当前用户, 查看该用户是否已经注册该考核
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        boolean registered = false;
        QueryWrapper<ContestRegister> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", cid).eq("user_id", accountProfile.getUid());
        ContestRegister exitContestRegister = contestRegisterService.getOne(queryWrapper);
        if (exitContestRegister != null) {
            registered = true;
        }

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("contest", contestVO);
        resMap.put("registered", registered);
        return resMap;
    }


    @Override
    public boolean registerContest(RegisterContestDTO registerContestDTO) {
        // 获取用户当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        Contest contest = contestService.getById(registerContestDTO.getCid());
        if (contest == null) {
            throw new StatusFailException("抱歉，该考核不存在！");
        }
        if (contest.getAuth().intValue() == Constants.Contest.AUTH_PRIVATE.getCode()) {
            if (StringUtils.isEmpty(registerContestDTO.getPassword())) {
                throw new StatusFailException("密码不能为空！请填写密码！");
            }
            if (!contest.getPwd().equals(registerContestDTO.getPassword())) {
                throw new StatusFailException("密码错误！请重试！");
            }
        }

        // 注册考核必须要填写真实姓名，学号和班级
        String realname = accountProfile.getRealname();
        String classe = accountProfile.getClasse();
        String number = accountProfile.getNumber();
        if (StringUtils.isEmpty(realname) || StringUtils.isEmpty(classe) || StringUtils.isEmpty(number)) {
            throw new StatusFailException("注册考核必须要填写真实姓名，学号和班级");
        }

        // 不能重复注册，查看是否已经注册
        QueryWrapper<ContestRegister> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", contest.getId()).eq("user_id", accountProfile.getId());
        ContestRegister exitContestRegister = contestRegisterService.getOne(queryWrapper);
        if (exitContestRegister != null) {
            throw new StatusFailException("您已经注册过该考核！请勿重复注册！");
        }

        boolean isOk = contestRegisterService.save(new ContestRegister()
                                            .setCid(contest.getId())
                                            .setUserId(accountProfile.getId()));

        return isOk;
    }


    @Override
    public List<ContestProblemVO> getContestProblem(Integer cid) {
        // 获取用户当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        Contest contest = contestService.getById(cid);
        if (contest == null) {
            throw new StatusFailException("抱歉，考核不存在！");
        }
        if (contest.getStatus().intValue() == Constants.Contest.STATUS_SCHEDULED.getCode()) {
            throw new StatusFailException("考核还未开始！");
        }

        List<ContestProblemVO> contestProblemVOList = contestProblemService.getContestProblem(cid, contest.getStartTime(), contest.getEndTime());

        // 判断当前用户是否ac该题目
        Map<Integer, Integer> isAcMap = new HashMap<>();
        List<Integer> pidList = new ArrayList<>();
        contestProblemVOList.forEach(cp -> {
            pidList.add(cp.getPid());
            isAcMap.put(cp.getPid(), 0);
            cp.setIsAc(false);
        });

        QueryWrapper<Judge> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct pid,status,submit_time,score")
                .in("pid", pidList)
                .eq("user_id", accountProfile.getUid())
                .eq("cid", cid)
                .orderByDesc("submit_time");

        List<Judge> judges = judgeService.list(queryWrapper);
        if (judges.size() > 0) {
            for (Judge judge : judges) {
                if (judge.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus()) {
                    isAcMap.put(judge.getPid(), 1);
                }
            }
        }

        for (ContestProblemVO contestProblemVO : contestProblemVOList) {
            if (isAcMap.get(contestProblemVO.getPid()) == 1) {
                contestProblemVO.setIsAc(true);
            }
        }

        return contestProblemVOList;
    }


    @Override
    public ProblemInfoVO getContestProblemDetails(Integer cid, Integer cpid) {

        Contest contest = contestService.getById(cid);
        if (contest == null) {
            throw new StatusFailException("抱歉，考核不存在！");
        }
        if (contest.getStatus().intValue() == Constants.Contest.STATUS_SCHEDULED.getCode()) {
            throw new StatusFailException("考核还未开始！");
        }

        // 查看该考核是否有该题目
        QueryWrapper<ContestProblem> queryWrapper = new QueryWrapper<>();
        ContestProblem contestProblem = contestProblemService.getById(cpid);
        if (contestProblem == null) {
            throw new StatusFailException("抱歉！该考核中不包含该题目！请刷新重试！");
        }

        // 查询题目详情，题目标签，题目语言，题目做题情况
        ProblemInfoVO problemInfo = frontProblemService.getProblemDetail(contestProblem.getPid(), cid);

        // 设置考核题目的标题为设置展示标题
        problemInfo.getProblem().setTitle(contestProblem.getDisplayTitle());

        return problemInfo;
    }


    @Override
    public IPage<JudgeVO> getContestSubmissionList(Integer limit,
                                                   Integer currentPage,
                                                   Boolean onlyMine,
                                                   Integer cid) {

        Contest contest = contestService.getById(cid);
        if (contest == null) {
            throw new StatusFailException("抱歉，考核不存在！");
        }
        if (contest.getStatus().intValue() == Constants.Contest.STATUS_SCHEDULED.getCode()) {
            throw new StatusFailException("考核还未开始！");
        }

        if (limit == null || limit < 1) limit = 10;
        if (currentPage == null || currentPage < 1) currentPage = 1;

        // 获取用户当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        Boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        Boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");
        String userId = null;
        if (onlyMine) {
            userId = accountProfile.getId();
        }

        IPage<JudgeVO> contestJudgePage = judgeService.getContestSubmissionList(limit, currentPage, cid, userId);
        if (contestJudgePage.getTotal() == 0) {
            return contestJudgePage;
        } else {
            // 比赛还是进行阶段，同时不是超级管理员与比赛管理员，需要将除自己之外的提交的时间、空间、长度隐藏
            if (contest.getStatus().intValue() == Constants.Contest.STATUS_RUNNING.getCode() && !isRoot && !isAdmin && !onlyMine) {
                contestJudgePage.getRecords().forEach(judgeVO -> {
                    judgeVO.setTime(null);
                    judgeVO.setMemory(null);
                    judgeVO.setLength(null);
                });
            }
        }
        return contestJudgePage;
    }


    @Override
    public IPage<AnnouncementVO> getContestAnnouncement(Integer limit,
                                                        Integer currentPage,
                                                        Integer cid) {

        if (limit == null || limit < 1) limit = 10;
        if (currentPage == null || currentPage < 1) currentPage = 1;
        IPage<AnnouncementVO> page = new Page<>(currentPage, limit);

        IPage<AnnouncementVO> announcementVOIPage = announcementMapper.getContestAnnouncement(page, cid);
        return announcementVOIPage;
    }


    @Override
    public IPage<JudgeVO> getContestUserSubmission(Integer limit, Integer currentPage, Integer cid) {
        if (limit == null || limit < 1) limit = 10;
        if (currentPage == null || currentPage < 1) currentPage = 1;

        // 判断考核是否存在
        Contest contest = contestService.getById(cid);
        if (contest == null) {
            throw new StatusFailException("该考核不存在！");
        }

        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        return judgeService.getContestSubmissionList(limit, currentPage, cid, accountProfile.getUid());
    }


    @Override
    public List<ContestVO> getRecentContest() {
        List<ContestVO> contestList = contestMapper.getRecentContest();
        setRegisterCount(contestList);
        return contestList;
    }


    private void setRegisterCount(List<ContestVO> contestList) {
        List<Integer> cidList = contestList.stream().map(ContestVO::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(cidList)) {
            // 默认的报名人数都为0
            contestList.forEach(contestVO -> contestVO.setCount(0));

            List<ContestRegisterCountVO> contestRegisterCountVOList = contestMapper.getContestRegisterCount(cidList);
            for (ContestRegisterCountVO contestRegisterCountVo : contestRegisterCountVOList) {
                for (ContestVO contestVo : contestList) {
                    if (contestRegisterCountVo.getCid().equals(contestVo.getId())) {
                        contestVo.setCount(contestRegisterCountVo.getCount());
                        break;
                    }
                }
            }
        }
    }
}
