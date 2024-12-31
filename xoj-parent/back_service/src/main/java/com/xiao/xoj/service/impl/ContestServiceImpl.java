package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.mapper.AnnouncementMapper;
import com.xiao.xoj.pojo.dto.AnnouncementDTO;
import com.xiao.xoj.pojo.dto.ContestProblemDTO;
import com.xiao.xoj.pojo.entity.announce.Announcement;
import com.xiao.xoj.pojo.entity.contest.*;
import com.xiao.xoj.mapper.ContestMapper;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.vo.AnnouncementVO;
import com.xiao.xoj.pojo.vo.ContestVO;
import com.xiao.xoj.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.shiro.AccountProfile;
import com.xiao.xoj.utils.ContestValidatorUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
@Service
public class ContestServiceImpl extends ServiceImpl<ContestMapper, Contest> implements ContestService {

    @Autowired
    private ContestProblemService contestProblemService;

    @Autowired
    private ContestAnnouncementService contestAnnouncementService;

    @Autowired
    private ContestRecordService contestRecordService;

    @Autowired
    private ContestRegisterService contestRegisterService;

    @Autowired
    private AnnouncementService announcementService;

    @Resource
    private AnnouncementMapper announcementMapper;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ContestValidatorUtil contestValidatorUtil;

    @Override
    public IPage<Contest> getContestList(Integer limit, Integer currentPage, String keyword) {

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        IPage<Contest> iPage = new Page<>(currentPage, limit);
        QueryWrapper<Contest> queryWrapper = new QueryWrapper<>();
        // 过滤密码
        if (!StringUtils.isEmpty(keyword)) {
            keyword = keyword.trim();
            queryWrapper
                    .like("title", keyword).or()
                    .like("id", keyword);
        }
        queryWrapper.orderByDesc("start_time");
        return this.page(iPage, queryWrapper);
    }

    @Override
    public Contest getContest(Integer cid) {
        Contest contest = this.getById(cid);
        if (contest == null) {
            throw new StatusFailException("查询失败：该比赛不存在,请检查参数cid是否准确！");
        }

        return contest;
    }

    @Override
    public boolean addContest(Contest contest) {
        // 校验参数
        contestValidatorUtil.checkParams(contest, true);

        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        contest.setUserId(accountProfile.getUid())
                .setUsername(accountProfile.getUsername());

        return this.save(contest);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteContest(Integer cid) {
        Contest contest = this.getById(cid);
        if (contest == null) {
            throw new StatusFailException("删除失败！未查询到该考核，请刷新重试！");
        }

        boolean isRemoveRegisterOk = contestRegisterService.remove(new UpdateWrapper<ContestRegister>().eq("cid", cid));
        boolean isRemoveRecord = contestRecordService.remove(new UpdateWrapper<ContestRecord>().eq("cid", cid));
        boolean isRemoveProblem = contestProblemService.remove(new UpdateWrapper<ContestProblem>().eq("cid", cid));
        boolean isRemoveContest = this.removeById(cid);

        // 删除公告
        List<ContestAnnouncement> caList = contestAnnouncementService.list(new QueryWrapper<ContestAnnouncement>().eq("cid",  cid));
        List<Integer> aIds = new LinkedList<>();
        if (caList.size() > 0) {
            caList.forEach(ca -> {
                aIds.add(ca.getAid());
            });
        }

        boolean isRemoveAnnouncement = true;
        boolean isRemoveContestAnnouncement = true;
        if (aIds.size() > 0) {
            isRemoveAnnouncement = announcementService.removeByIds(aIds);
            isRemoveContestAnnouncement = contestAnnouncementService.remove(new UpdateWrapper<ContestAnnouncement>().eq("cid", cid));
        }


        if (isRemoveRegisterOk && isRemoveRecord && isRemoveProblem
            && isRemoveContest && isRemoveAnnouncement && isRemoveContestAnnouncement) {
            return true;
        }
        return false;
    }


    @Override
    public boolean updateContest(Contest contest) {
        // 校验参数
        contestValidatorUtil.checkParams(contest, false);

        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        contest.setUserId(accountProfile.getUid())
                .setUsername(accountProfile.getUsername());

        return this.updateById(contest);
    }


    @Override
    public IPage<Problem> getProblemList(Integer limit, Integer currentPage, String keyword, Long cid) {
        if (limit == null || limit < 1) limit = 10;
        if (currentPage == null || currentPage < 1) currentPage = 1;

        // 查询ContestProblem集合
        QueryWrapper<ContestProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", cid);
        List<ContestProblem> contestProblemList = contestProblemService.list(queryWrapper);
        if (contestProblemList.size() == 0) {
            return null;
        }


        List<Integer> pidList = new LinkedList<>();
        Map<Integer, String> cpMap = new HashMap<>();
        contestProblemList.forEach(cp -> {
            pidList.add(cp.getPid());
            cpMap.put(cp.getPid(), cp.getDisplayId());
        });

        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.in("id", pidList);
        IPage<Problem> page = new Page<>(currentPage, limit);
        IPage<Problem> problemIPage = problemService.page(page, problemQueryWrapper);

        if (problemIPage.getTotal() > 0) {
            // 根据displayId排序
            List<Problem> problemList = problemIPage.getRecords();
            List<Problem> sortProblemList = problemList.stream().sorted(Comparator.comparing(Problem::getId, (a, b) -> {
                String aDisplayId = cpMap.get(a);
                String bDisplayId = cpMap.get(b);
                return aDisplayId.compareTo(bDisplayId);
            })).collect(Collectors.toList());

            // 设置displayId
            sortProblemList.forEach(p -> p.setProblemId(cpMap.get(p.getId())));
            problemIPage.setRecords(sortProblemList);
        }

        return problemIPage;
    }


    @Override
    public boolean addProblemFromPublic(ContestProblemDTO contestProblemDto) {
        // 查询考核和题目是否存在
        Contest contest = this.getById(contestProblemDto.getCid());
        if (contest == null) {
            throw new StatusFailException("考核不存在！请重试！");
        }
        Problem problem = problemService.getById(contestProblemDto.getPid());
        if (problem == null) {
            throw new StatusFailException("题目不存在！请重试！");
        }

        // 查询该题目是否已经添加
        QueryWrapper<ContestProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", contest.getId()).eq("pid", problem.getId());
        ContestProblem contestProblem = contestProblemService.getOne(queryWrapper);
        if (contestProblem != null) {
            throw new StatusFailException("该题目已经添加过！请勿重复添加！");
        }

        contestProblem = new ContestProblem()
                .setCid(contest.getId())
                .setPid(problem.getId())
                .setDisplayId(contestProblemDto.getDisplayId());

        if (!StringUtils.isEmpty(contestProblemDto.getDisplayTitle())) {
            contestProblem.setDisplayTitle(contestProblemDto.getDisplayTitle());
        } else {
            contestProblem.setDisplayTitle(problem.getTitle());
        }

        return contestProblemService.save(contestProblem);
    }


    @Override
    public boolean updateProblem(ContestProblemDTO contestProblemDto) {
        // 查询考核和题目是否存在
        Contest contest = this.getById(contestProblemDto.getCid());
        if (contest == null) {
            throw new StatusFailException("考核不存在！请重试！");
        }
        Problem problem = problemService.getById(contestProblemDto.getPid());
        if (problem == null) {
            throw new StatusFailException("题目不存在！请重试！");
        }

        QueryWrapper<ContestProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", contest.getId()).eq("pid", problem.getId());
        ContestProblem contestProblem = contestProblemService.getOne(queryWrapper);

        contestProblem.setDisplayId(contestProblemDto.getDisplayId());
        if (!StringUtils.isEmpty(contestProblemDto.getDisplayTitle())) {
            contestProblem.setDisplayTitle(contestProblemDto.getDisplayTitle());
        } else {
            contestProblem.setDisplayTitle(problem.getTitle());
        }

        return contestProblemService.updateById(contestProblem);
    }


    @Override
    public boolean removeProblem(Integer cid, Integer pid) {
        QueryWrapper<ContestProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", cid).eq("pid", pid);
        ContestProblem contestProblem = contestProblemService.getOne(queryWrapper);
        if (contestProblem == null) {
            throw new StatusFailException("该考核题目不存在！请刷新重试！");
        }

        // 移除考核题目
        boolean isOk = contestProblemService.removeById(contestProblem.getId());
        return isOk;
    }


    @Override
    public IPage<AnnouncementVO> getAnnouncementList(Integer limit, Integer currentPage, Integer cid) {
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        IPage<AnnouncementVO> page = new Page<>(currentPage, limit);
        IPage<AnnouncementVO> announcementPage = announcementMapper.getContestAnnouncement(page, cid);
        return announcementPage;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addAnnouncement(AnnouncementDTO announcementDto) {
        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        // 参数校验
        Contest contest = this.getById(announcementDto.getCid());

        Announcement announcement = announcementDto.getAnnouncement();
        announcement.setUserId(accountProfile.getUid());
        if (contest == null) {
            throw new StatusFailException("添加失败！未查询到该考核！");
        }
        if (announcement.getTitle() == null || announcement.getTitle().length() <= 0) {
            throw new StatusFailException("公告标题不能为空！");
        }
        if (announcement.getContent() == null || announcement.getContent().length() <= 0) {
            throw new StatusFailException("公告内容不能为空！");
        }

        boolean isSaveAnnouncement = announcementService.save(announcementDto.getAnnouncement());
        boolean isSaveContestAnnouncement = contestAnnouncementService.save(new ContestAnnouncement()
                                                                                .setAid(announcement.getId())
                                                                                .setCid(contest.getId()));
        if (!isSaveAnnouncement || !isSaveContestAnnouncement)
            return false;
        return true;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAnnouncement(Integer aid) {
        Announcement announcement = announcementService.getById(aid);
        if (announcement == null) {
            throw new StatusFailException("删除失败！公告不存在！");
        }

        // 删除announcement和contestAnnouncement表中的数据
        boolean isRemoveAnnouncement = announcementService.removeById(aid);
        UpdateWrapper<ContestAnnouncement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("aid", aid);
        boolean isRemoveContestAnnouncement = contestAnnouncementService.remove(updateWrapper);

        if (isRemoveAnnouncement && isRemoveContestAnnouncement)
            return true;
        return false;
    }


    @Override
    public boolean updateAnnouncement(AnnouncementDTO announcementDto) {
        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        // 参数校验
        Contest contest = this.getById(announcementDto.getCid());

        Announcement announcement = announcementDto.getAnnouncement();
        announcement.setUserId(accountProfile.getUid());

        if (contest == null) {
            throw new StatusFailException("修改失败！未查询到该公告！");
        }
        if (announcement.getTitle() == null || announcement.getTitle().length() <= 0) {
            throw new StatusFailException("公告标题不能为空！");
        }
        if (announcement.getContent() == null || announcement.getContent().length() <= 0) {
            throw new StatusFailException("公告内容不能为空！");
        }

        boolean isSaveAnnouncement = announcementService.updateById(announcement);
        if (!isSaveAnnouncement)
            return false;
        return true;
    }


}
