package com.xiao.xoj.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.common.exception.StatusNotFoundException;
import com.xiao.xoj.mapper.CommentMapper;
import com.xiao.xoj.pojo.entity.discussion.Comment;
import com.xiao.xoj.pojo.entity.discussion.CommentLike;
import com.xiao.xoj.pojo.entity.discussion.Discussion;
import com.xiao.xoj.pojo.entity.discussion.Reply;
import com.xiao.xoj.pojo.vo.CommentListVO;
import com.xiao.xoj.pojo.vo.CommentVO;
import com.xiao.xoj.service.CommentLikeService;
import com.xiao.xoj.service.CommentService;
import com.xiao.xoj.service.DiscussionService;
import com.xiao.xoj.service.ReplyService;
import com.xiao.xoj.service.front.FrontCommentService;
import com.xiao.xoj.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/5/24 11:27
 * @description: TODO
 */
@Service
public class FrontCommentServiceImpl implements FrontCommentService {

    @Resource
    private CommentService commentService;

    @Resource
    private DiscussionService discussionService;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private CommentLikeService commentLikeService;

    @Resource
    private ReplyService replyService;

    @Override
    public CommentListVO getCommentList(Integer limit, Integer currentPage, Integer did) {

        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        Page<CommentVO> page = new Page<>(currentPage, limit);
        IPage<CommentVO> commentList = commentMapper.getCommentList(page, did);

        // 如果登录了，判断是否点赞了
        HashMap<Integer, Boolean> commentLikeMap = new HashMap<>();
        if (accountProfile != null) {
            List<Integer> commentIdList = new LinkedList<>();
            for (CommentVO commentVO : commentList.getRecords()) {
                commentIdList.add(commentVO.getId());
            }

            if (commentIdList.size() > 0) {
                QueryWrapper<CommentLike> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", accountProfile.getId()).in("comment_id", commentIdList);

                List<CommentLike> commentLikes = commentLikeService.list(queryWrapper);

                // 如果用户点赞过该评论，记录到map中
                for (CommentLike commentLike : commentLikes) {
                    commentLikeMap.put(commentLike.getCommentId(), true);
                }
            }
        }

        CommentListVO commentListVO = new CommentListVO();
        commentListVO.setCommentList(commentList);
        commentListVO.setCommentLikeMap(commentLikeMap);
        return commentListVO;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO addComment(Comment comment) {
        // 参数校验
        if (comment.getContent() == null || comment.getContent().length() > 10000) {
            throw new StatusFailException("评论内容不能为空或者内容长度超过限制（10000）");
        }
        if (comment.getDiscussionId() == null) {
            throw new StatusFailException("请求参数有误！请重试！");
        }
        Discussion discussion = discussionService.getById(comment.getDiscussionId());
        if (discussion == null) {
            throw new StatusFailException("该贴子不存在！");
        }

        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        comment.setFromAvatar(accountProfile.getAvatar())
                .setFromUserId(accountProfile.getUid())
                .setFromUsername(accountProfile.getUsername());

        // todo 带有表情的字符串转换为编码

        boolean isOk = commentService.saveOrUpdate(comment);

        if (isOk) {
            CommentVO commentVO = new CommentVO();
            commentVO.setContent(comment.getContent());
            commentVO.setId(comment.getId());
            commentVO.setFromAvatar(comment.getFromAvatar());
            commentVO.setFromUsername(comment.getFromUsername());
            commentVO.setFromUserId(comment.getFromUserId());
            commentVO.setLikeNum(0);
            commentVO.setGmtCreate(comment.getGmtCreate());
            commentVO.setReplyList(new LinkedList<>());
            // 更新贴子的回复数
            discussion.setCommentNum(discussion.getCommentNum() + 1);
            discussionService.saveOrUpdate(discussion);

            // todo 更新消息

            return commentVO;
        } else {
            throw new StatusFailException("评论失败！请重新尝试！");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Integer cid) {
        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        Comment comment = commentService.getById(cid);
        if (comment == null) {
            throw new StatusNotFoundException("该评论不存在！");
        }

        // 判断是否有权限删除：管理员和发布者可以删除
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");
        boolean isAuthor = accountProfile.getId().equals(comment.getFromUserId());
        if (!isRoot && !isAdmin && !isAuthor) {
            throw new StatusFailException("删除失败！您没有权限删除！");
        }

        // 删除评论点赞
        boolean isOkDeleteCommentLike = commentLikeService.remove(new UpdateWrapper<CommentLike>()
                .eq("user_id", accountProfile.getId())
                .eq("comment_id", comment.getId()));

        // 需要删除的评论回复数
        int replyNum = replyService.count(new QueryWrapper<Reply>().eq("comment_id", cid));

        // 删除该评论下的回复
        boolean isOkDeleteReply = replyService.remove(new UpdateWrapper<Reply>().eq("comment_id", comment.getId()));

        // 贴子评论数 - replyNum
        Discussion discussion = discussionService.getById(comment.getDiscussionId());
        discussion.setCommentNum(discussion.getCommentNum() - replyNum);
        boolean isOkDeleteCommentNum = discussionService.saveOrUpdate(discussion);

        boolean isOkDeleteComment = commentService.removeById(cid);

        if (isOkDeleteCommentNum && isOkDeleteCommentLike && isOkDeleteReply && isOkDeleteComment) {
            return true;
        }
        return false;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCommentLike(Integer cid, Boolean toLike) {
        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        QueryWrapper<CommentLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment_id", cid).eq("user_id", accountProfile.getId());
        CommentLike commentLike = commentLikeService.getOne(queryWrapper);

        if (toLike) { // 点赞
            if (commentLike == null) {
                boolean isSave = commentLikeService.save(new CommentLike().setCommentId(cid).setUserId(accountProfile.getId()));
                if (!isSave) {
                    throw new StatusFailException("点赞失败！请重试！");
                }
                // 点赞 + 1
                Comment comment = commentService.getById(cid);
                comment.setLikeNum(comment.getLikeNum() + 1);
                commentService.updateById(comment);

                // todo 发送点赞消息
            }
        }  else { // 取消点赞
            if (commentLike != null) {
                boolean isDelete = commentLikeService.removeById(commentLike.getId());
                if (!isDelete) {
                    throw new StatusFailException("取消点赞失败！请重试！");
                }
                // 点赞 - 1
                Comment comment = commentService.getById(cid);
                comment.setLikeNum(comment.getLikeNum() - 1);
                commentService.updateById(comment);
            }
        }
        return true;
    }



    @Override
    public List<Reply> getReplyList(Integer cid) {
        QueryWrapper<Reply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment_id", cid).orderByDesc("gmt_create");
        return replyService.list(queryWrapper);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reply addReply(Reply reply) {
        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        // 参数校验
        if (reply.getCommentId() == null) {
            throw new StatusFailException("参数有误");
        }
        Comment comment = commentService.getById(reply.getCommentId());
        if (comment == null) {
            throw new StatusNotFoundException("该评论不存在，无法评论");
        }
        if (reply.getContent() == null || reply.getContent().length() > 10000) {
            throw new StatusFailException("回复内容不能为空或者内容长度超过限制（10000）");
        }


        reply.setFromAvatar(accountProfile.getAvatar())
                .setFromUserId(accountProfile.getUid())
                .setFromUsername(accountProfile.getUsername());

        // todo 带有表情的字符串转换为编码

        boolean isOk = replyService.save(reply);
        if (!isOk) {
            throw new StatusFailException("回复失败，请重试！");
        } else {
            Discussion discussion = discussionService.getById(comment.getDiscussionId());
            discussion.setCommentNum(discussion.getCommentNum() + 1);
            discussionService.updateById(discussion);
            // todo 更新消息
        }

        return reply;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteReply(Integer rid) {
        // 获取当前用户信息
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        Reply reply = replyService.getById(rid);
        if (reply == null) {
            throw new StatusFailException("删除失败！该回复不存在！");
        }

        // 判断是否有权限删除：管理员和发布者可以删除
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");
        boolean isAuthor = accountProfile.getId().equals(reply.getFromUserId());
        if (!isRoot && !isAdmin && !isAuthor) {
            throw new StatusFailException("删除失败！您没有权限删除！");
        }

        Comment comment = commentService.getById(reply.getCommentId());
        Discussion discussion = discussionService.getById(comment.getDiscussionId());

        boolean isDeleteReply = replyService.removeById(reply.getId());
        if (isDeleteReply) {
            discussion.setCommentNum(discussion.getCommentNum() - 1);
            discussionService.updateById(discussion);
        } else {
            throw new StatusFailException("删除失败！请重试！");
        }

        return isDeleteReply;
    }

}
