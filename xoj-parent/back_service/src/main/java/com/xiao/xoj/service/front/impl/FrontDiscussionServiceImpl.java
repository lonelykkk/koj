package com.xiao.xoj.service.front.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.common.exception.StatusForbiddenException;
import com.xiao.xoj.common.exception.StatusNotFoundException;
import com.xiao.xoj.mapper.DiscussionMapper;
import com.xiao.xoj.pojo.entity.discussion.Discussion;
import com.xiao.xoj.pojo.entity.discussion.DiscussionLike;
import com.xiao.xoj.pojo.entity.discussion.DiscussionReport;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.vo.DiscussionVO;
import com.xiao.xoj.service.DiscussionLikeService;
import com.xiao.xoj.service.DiscussionReportService;
import com.xiao.xoj.service.DiscussionService;
import com.xiao.xoj.service.ProblemService;
import com.xiao.xoj.service.front.FrontDiscussionService;
import com.xiao.xoj.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author 肖恩
 * @create 2023/5/24 11:27
 * @description: TODO
 */
@Service
public class FrontDiscussionServiceImpl implements FrontDiscussionService {

    @Resource
    private DiscussionService discussionService;

    @Resource
    private ProblemService problemService;

    @Resource
    private DiscussionMapper discussionMapper;

    @Resource
    private DiscussionLikeService discussionLikeService;

    @Resource
    private DiscussionReportService discussionReportService;

    @Override
    public IPage<Discussion> getDiscussionList(Integer limit,
                                               Integer currentPage,
                                               Integer categoryId,
                                               Integer pid,
                                               Boolean onlyMine,
                                               String keyword,
                                               Boolean isAdmin) {

        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        IPage<Discussion> iPage = new Page<>(currentPage, limit);

        QueryWrapper<Discussion> queryWrapper = new QueryWrapper<>();

        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }

        if (!StringUtils.isEmpty(keyword)) {
            final String key = keyword.trim();

            queryWrapper.and(wrapper -> wrapper.like("title", key).or()
                    .like("author", key).or()
                    .like("id", key).or()
                    .like("description", key));
        }

        if (pid != null && pid != 0) {
            queryWrapper.eq("problem_id", pid);
        }

        queryWrapper
                .eq(!isAdmin, "is_disabled", 0)
                .orderByDesc("top_priority")
                .orderByDesc("gmt_create")
                .orderByDesc("like_num")
                .orderByDesc("view_num");


        if (onlyMine && accountProfile != null) {
            queryWrapper.eq("user_id", accountProfile.getId());
        }

        IPage<Discussion> queryRes = discussionService.page(iPage, queryWrapper);
        return queryRes;
    }



    @Override
    public DiscussionVO getDiscussionDetail(Integer did) {

        // 获取当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        Discussion discussion = discussionService.getById(did);

        // 判断当前用户是否点赞了该贴子
        String uid = null;
        if (accountProfile != null) {
            uid = accountProfile.getId();
        }
        DiscussionVO discussionVO = discussionMapper.getDiscussion(did, uid);


        if (discussionVO == null) {
            throw new StatusNotFoundException("对不起，该讨论不存在！");
        }
        if (discussionVO.getIsDisabled()) {
            throw new StatusForbiddenException("对不起，该贴子已被封禁！");
        }

        // 浏览量 + 1
        UpdateWrapper<Discussion> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("view_num=view_num+1").eq("id", discussionVO.getId());
        discussionService.update(discussion, updateWrapper);
        discussionVO.setViewNum(discussionVO.getViewNum()  + 1);

        return discussionVO;
    }

    @Override
    public boolean addDiscussion(Discussion discussion) {
        // 参数校验
        if (StrUtil.isEmpty(discussion.getTitle()) || discussion.getTitle().length() > 255) {
            throw new StatusFailException("贴子标题为空或者标题长度超过限制（255）");
        }
        if (StrUtil.isEmpty(discussion.getDescription()) || discussion.getDescription().length() > 255) {
            throw new StatusFailException("贴子描述为空或者描述长度超过限制（255）");
        }
        if (StrUtil.isEmpty(discussion.getContent()) || discussion.getContent().length() > 65535) {
            throw new StatusFailException("贴子内容为空或者内容长度超过限制（65535）");
        }

        // 获取当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Integer problemId = discussion.getProblemId();
        if (problemId != null && problemId != 0) {
            Problem problem = problemService.getById(problemId);
            if (problem == null) {
                throw new StatusFailException("对不起，该题目不存在，无法发布题解!");
            }
        }

        // todo 发帖限制

        discussion.setAuthor(accountProfile.getUsername())
                .setAvatar(accountProfile.getAvatar())
                .setUserId(accountProfile.getId());

        // 如果不是管理员角色，一律重置为不置顶
        if (!isRoot || !isAdmin) {
            discussion.setTopPriority(false);
        }

        return discussionService.save(discussion);
    }


    @Override
    public boolean deleteDiscussion(Integer did) {
        // 获取当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        Discussion discussion = discussionService.getById(did);
        if (discussion == null) {
            throw new StatusNotFoundException("删除失败！该贴子不存在！");
        }

        // 判断是否有删除权限：只有管理员和贴子发布者可以删除
        if (!SecurityUtils.getSubject().hasRole("root") &&
            !SecurityUtils.getSubject().hasRole("admin") &&
            !accountProfile.getId().equals(discussion.getUserId())) {
            throw new StatusFailException("删除失败！您无权限操作！");
        }

        // todo 删除该贴子的评论和评论回复

        return discussionService.removeById(did);
    }


    @Override
    public boolean updateDiscussion(DiscussionVO discussionVO) {
        // 参数校验
        if (StrUtil.isEmpty(discussionVO.getTitle()) || discussionVO.getTitle().length() > 255) {
            throw new StatusFailException("贴子标题为空或者标题长度超过限制（255）");
        }
        if (StrUtil.isEmpty(discussionVO.getDescription()) || discussionVO.getDescription().length() > 255) {
            throw new StatusFailException("贴子描述为空或者描述长度超过限制（255）");
        }
        if (StrUtil.isEmpty(discussionVO.getContent()) || discussionVO.getContent().length() > 65535) {
            throw new StatusFailException("贴子内容为空或者内容长度超过限制（65535）");
        }

        // 获取当前登录用户，判断是否有权限操作
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        if (!SecurityUtils.getSubject().hasRole("root") &&
            !SecurityUtils.getSubject().hasRole("admin") &&
            !accountProfile.getId().equals(discussionVO.getUid())) {
            throw new StatusFailException("修改失败！您无权限操作！");
        }

        Discussion discussion = new Discussion();
        discussion.setId(discussionVO.getId());
        discussion.setCategoryId(discussionVO.getCategoryId());
        discussion.setTitle(discussionVO.getTitle());
        discussion.setDescription(discussionVO.getDescription());
        discussion.setContent(discussionVO.getContent());
        discussion.setProblemId(discussionVO.getPid());
        discussion.setTopPriority(discussionVO.getTopPriority());

        return discussionService.updateById(discussion);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addDiscussionLike(Integer did, Boolean toLike) {
        // 获取当前登录用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        QueryWrapper<DiscussionLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("discussion_id", did).eq("user_id", accountProfile.getId());

        Discussion discussion = discussionService.getById(did);
        DiscussionLike discussionLike = discussionLikeService.getOne(queryWrapper);

        if (toLike) { // 添加点赞
            if (discussionLike == null) { // // 如果不存在就添加
                boolean isOk = discussionLikeService.saveOrUpdate(new DiscussionLike()
                                                                        .setUserId(accountProfile.getId())
                                                                        .setDiscussionId(did));
                if (!isOk) {
                    throw new StatusFailException("点赞失败,请重新尝试！");
                }
                // 点赞 + 1
                UpdateWrapper<Discussion> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", discussion.getId()).setSql("like_num=like_num+1");
                discussionService.update(discussion, updateWrapper);

                // todo 发送点赞消息 当前帖子要不是点赞者的 才发送点赞消息
            }

        } else { // 取消点赞
            if (discussionLike != null) { // 如果存在就删除
                boolean isOk = discussionLikeService.removeById(discussionLike.getId());
                if (!isOk) {
                    throw new StatusFailException("取消点赞失败，请重试尝试！");
                }
                // 点赞 - 1
                UpdateWrapper<Discussion> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", discussion.getId()).setSql("like_num=like_num-1");
                discussionService.update(discussion, updateWrapper);
            }
        }

        return true;
    }

    @Override
    public boolean addDiscussionReport(DiscussionReport discussionReport) {

        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        if (discussionReport.getDiscussionId() == null) {
            throw new StatusFailException("参数有误！请重试！");
        }
        Discussion discussion = discussionService.getById(discussionReport.getDiscussionId());
        if (discussion == null) {
            throw new StatusFailException("举报失败！该贴子不存在或者已经被删除！");
        }
        if (StringUtils.isEmpty(discussionReport.getContent())) {
            throw new StatusFailException("举报内容不能为空");
        }

        discussionReport.setUserId(accountProfile.getUid());
        return discussionReportService.save(discussionReport);

    }

}
