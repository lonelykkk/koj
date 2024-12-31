package com.xiao.xoj.service.front;

import com.xiao.xoj.pojo.entity.discussion.Comment;
import com.xiao.xoj.pojo.entity.discussion.Reply;
import com.xiao.xoj.pojo.vo.CommentListVO;
import com.xiao.xoj.pojo.vo.CommentVO;

import java.util.List;

public interface FrontCommentService {
    CommentListVO getCommentList(Integer limit, Integer currentPage, Integer did);

    CommentVO addComment(Comment comment);

    boolean deleteComment(Integer cid);

    boolean addCommentLike(Integer cid, Boolean toLike);

    List<Reply> getReplyList(Integer cid);

    Reply addReply(Reply reply);

    boolean deleteReply(Integer rid);
}
