package com.xiao.xoj.controller.front;

import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.discussion.Comment;
import com.xiao.xoj.pojo.entity.discussion.Reply;
import com.xiao.xoj.pojo.vo.CommentListVO;
import com.xiao.xoj.pojo.vo.CommentVO;
import com.xiao.xoj.service.front.FrontCommentService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 肖恩
 * @create 2023/5/24 11:19
 * @description: 评论，回复，点赞，举报控制器
 */
@RestController
@RequestMapping("/xoj/front-comment")
public class FrontCommentController {

    @Autowired
    private FrontCommentService frontCommentService;


    /**
     * @des: 获取贴子评论列表
     *
     * @param limit
     * @param currentPage
     * @param did
     * @return
     */
    @GetMapping("get-comment-list")
    public ResultData<CommentListVO> getCommentList(@RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                                                    @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                                    @RequestParam(value = "did", required = true) Integer did) {

        CommentListVO commentListVO = frontCommentService.getCommentList(limit, currentPage, did);
        return ResultData.success(commentListVO);
    }


    /**
     * @des: 发布评论
     *
     * @param comment
     * @return
     */
    @PostMapping
    @RequiresAuthentication
    public ResultData<CommentVO> addComment(@RequestBody Comment comment) {
        CommentVO commentVO = frontCommentService.addComment(comment);
        if (commentVO == null) {
            return ResultData.fail("评论失败！");
        }
        return ResultData.success(commentVO, "评论成功");
    }



    /**
     * @des: 删除评论，评论下面的回复也需要删除
     *
     * @param cid
     * @return
     */
    @DeleteMapping
    @RequiresAuthentication
    public ResultData<Void> deleteComment(@RequestParam(value = "cid") Integer cid) {
        boolean isOk = frontCommentService.deleteComment(cid);
        if (!isOk) {
            return ResultData.fail("删除失败！请重新尝试！");
        }
        return ResultData.success("删除成功！");
    }


    /**
     * @des: 点赞评论
     *
     * @param cid
     * @param toLike
     * @return
     */
    @PostMapping("comment-like")
    @RequiresAuthentication
    public ResultData<Void> addCommentLike(@RequestParam(value = "cid") Integer cid,
                                           @RequestParam(value = "toLike") Boolean toLike) {
        boolean isOk = frontCommentService.addCommentLike(cid, toLike);
        if (!isOk) {
            return ResultData.fail("点赞失败，请重试！");
        }
        return ResultData.success("点赞成功！");
    }


    /**
     * @des: 获取评论回复列表
     *
     * @param cid   评论id
     * @return
     */
    @GetMapping("get-reply-list")
    public ResultData<List<Reply>> getReplyList(@RequestParam(value = "cid") Integer cid) {
        List<Reply> replies = frontCommentService.getReplyList(cid);
        return ResultData.success(replies);
    }


    /**
     * @des: 添加回复评论
     *
     * @param reply
     * @return
     */
    @PostMapping("reply")
    @RequiresAuthentication
    public ResultData<Reply> addReply(@RequestBody Reply reply) {
        Reply resReply = frontCommentService.addReply(reply);
        return ResultData.success(resReply);
    }


    /**
     * @des: 删除回复评论
     *
     * @param rid
     * @return
     */
    @DeleteMapping("reply")
    @RequiresAuthentication
    public ResultData<Void> deleteReply(@RequestParam(value = "rid") Integer rid) {
        boolean isOk = frontCommentService.deleteReply(rid);
        if (!isOk) {
            return ResultData.fail("删除失败！请重试！");
        }
        return ResultData.success("删除成功！");
    }
}
