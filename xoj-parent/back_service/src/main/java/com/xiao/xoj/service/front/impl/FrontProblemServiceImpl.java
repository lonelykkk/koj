package com.xiao.xoj.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.mapper.JudgeMapper;
import com.xiao.xoj.mapper.ProblemMapper;
import com.xiao.xoj.pojo.entity.contest.Contest;
import com.xiao.xoj.pojo.entity.judge.Judge;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.entity.problem.ProblemDescription;
import com.xiao.xoj.pojo.entity.problem.Tag;
import com.xiao.xoj.pojo.vo.ProblemCountVO;
import com.xiao.xoj.pojo.vo.ProblemInfoVO;
import com.xiao.xoj.pojo.vo.ProblemVO;
import com.xiao.xoj.service.*;
import com.xiao.xoj.service.front.FrontProblemService;
import com.xiao.xoj.shiro.AccountProfile;
import com.xiao.xoj.utils.Constants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 肖恩
 * @create 2023/4/18 20:51
 * @description: TODO
 */
@Service
public class FrontProblemServiceImpl implements FrontProblemService {

    @Resource
    private ProblemMapper problemMapper;

    @Resource
    private ProblemService problemService;

    @Resource
    private ProblemDescriptionService problemDescriptionService;

    @Resource
    private TagService tagService;

    @Resource
    private ContestService contestService;

    @Resource
    private JudgeMapper judgeMapper;

    @Override
    public Page<ProblemVO> getProblemList(Integer limit, Integer currentPage, String keyword, List<Integer> tagId, Integer difficulty, Integer type) {
        if (limit == null || limit < 1) limit = 10;
        if (currentPage == null || currentPage < 1) currentPage = 1;

        if (!StringUtils.isEmpty(keyword)) {
            keyword = keyword.trim(); // 去除首尾空格
        }

        if (tagId != null) {
            tagId = tagId.stream().distinct().collect(Collectors.toList());
        }

        Page<ProblemVO> page = new Page<>(currentPage, limit);
        List<ProblemVO> pList = problemMapper.getProblemList(page, keyword, difficulty, tagId, type);
        List<Integer> pidList = pList.stream().map(ProblemVO::getPid).collect(Collectors.toList());

        // 设置total和ac数
        if (pList.size() > 0) {
            List<ProblemCountVO> problemCountList = judgeMapper.getProblemListCount(pidList);
            for (ProblemVO problemVO : pList) {
                problemVO.setIsAc(false);
                for (ProblemCountVO problemCountVO : problemCountList) {
                    if (problemVO.getPid().equals(problemCountVO.getPid())) {
                        problemVO.setTotal(problemCountVO.getTotal());
                        problemVO.setAc(problemCountVO.getAc());
                    }
                }
            }
        }


        // 判断用户是否登录
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            // 获取当前用户信息
            AccountProfile accountProfile = (AccountProfile) subject.getPrincipal();

            Map<Integer, Integer> isAcMap = new HashMap<>();
            for (Integer pid : pidList) isAcMap.put(pid, 0);

            // 查询用户是否ac该题目
            QueryWrapper<Judge> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("distinct pid,status,submit_time,score")
                    .in("pid", pidList)
                    .eq("user_id", accountProfile.getUid())
                    .eq("cid", 0)
                    .orderByDesc("submit_time");

            List<Judge> judges = judgeMapper.selectList(queryWrapper);
            if (judges.size() > 0) {
                for (Judge judge : judges) {
                    if (judge.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus()) {
                        isAcMap.put(judge.getPid(), 1);
                    }
                }
            }

            for (ProblemVO problemVO : pList) {
                if (isAcMap.get(problemVO.getPid()) == 1) {
                    problemVO.setIsAc(true);
                }
            }
        }

        return page.setRecords(pList);
    }



    @Override
    public ProblemInfoVO getProblemDetail(Integer pid, Integer cid) {

        // 查询题目详情
        Problem problem = problemService.getById(pid);
        if (problem == null) {
            throw new StatusFailException("该题目不存在！");
        }

        // 查询题目描述
        QueryWrapper<ProblemDescription> pdWrapper = new QueryWrapper<>();
        pdWrapper.eq("problem_id", pid);
        ProblemDescription description = problemDescriptionService.getOne(pdWrapper);
        if (description == null) {
            throw new StatusFailException("题目描述为空！请联系管理员！");
        }

        // 查询题目标签
        List<Tag> tagList = tagService.getTagByPid(pid);


        // 查询做题情况，total数和ac数
        ProblemCountVO problemCountVO = null;

        if (cid == null) {
            // 普通题目做题情况
            problemCountVO = judgeMapper.getProblemCount(pid);
        } else {
            // 考核题目做题情况，考核结束之后就不再统计
            Contest contest = contestService.getById(cid);
            problemCountVO = judgeMapper.getContestProblemCount(pid, cid, contest.getStartTime());
        }
        ProblemInfoVO problemInfoVO = new ProblemInfoVO(problem, description.getDescription(), tagList, problemCountVO);

        return problemInfoVO;
    }
}
