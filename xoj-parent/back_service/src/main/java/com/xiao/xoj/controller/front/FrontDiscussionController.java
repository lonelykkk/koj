package com.xiao.xoj.controller.front;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.discussion.Discussion;
import com.xiao.xoj.pojo.entity.discussion.DiscussionReport;
import com.xiao.xoj.pojo.vo.DiscussionVO;
import com.xiao.xoj.service.front.FrontDiscussionService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 肖恩
 * @create 2023/5/24 11:21
 * @description: 贴子讨论相关控制器
 */
@RestController
@RequestMapping("/xoj/front-discussion")
public class FrontDiscussionController {

    @Autowired
    private FrontDiscussionService frontDiscussionService;

    /**
     * @des: 获取贴子列表
     *
     * @param limit
     * @param currentPage
     * @param categoryId
     * @param pid
     * @param onlyMine
     * @param keyword
     * @return
     */
    @GetMapping("get-discussion-list")
    public ResultData<IPage<Discussion>> getDiscussionList(@RequestParam(value = "limit", required = false) Integer limit,
                                                           @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                           @RequestParam(value = "cid", required = false) Integer categoryId,
                                                           @RequestParam(value = "pid", required = false) Integer pid,
                                                           @RequestParam(value = "onlyMine", required = false, defaultValue = "false") Boolean onlyMine,
                                                           @RequestParam(value = "keyword", required = false) String keyword,
                                                           @RequestParam(value = "isAdmin", required = false, defaultValue = "false") Boolean isAdmin) {
        IPage<Discussion> result = frontDiscussionService.getDiscussionList(limit, currentPage, categoryId, pid, onlyMine, keyword, isAdmin);
        return ResultData.success(result);
    }


    /**
     * @des: 获取贴子详情
     *
     * @param did
     * @return
     */
    @GetMapping("get-discussion-detail")
    public ResultData<DiscussionVO> getDiscussionDetail(@RequestParam(value = "did", required = true) Integer did) {
        DiscussionVO result = frontDiscussionService.getDiscussionDetail(did);
        return ResultData.success(result);
    }


    /**
     * @des: 发布贴子
     *
     * @param discussion
     * @return
     */
    @PostMapping()
    @RequiresAuthentication
    public ResultData<Void> addDiscussion(@RequestBody Discussion discussion) {
        boolean isOk = frontDiscussionService.addDiscussion(discussion);
        if (!isOk) {
            return ResultData.fail("发布失败");
        }
        return ResultData.success("发布成功");
    }


    /**
     * @des: 删除贴子，贴子下面的评论及评论回复也需要删除
     *
     * @param did
     * @return
     */
    @DeleteMapping
    @RequiresAuthentication
    public ResultData<Void> deleteDiscussion(@RequestParam(value = "did", required = true) Integer did) {
        boolean isOk = frontDiscussionService.deleteDiscussion(did);
        if (!isOk) {
            return ResultData.fail("删除失败");
        }
        return ResultData.success("删除成功");
    }


    /**
     * @des: 修改贴子
     *
     * @param discussionVO
     * @return
     */
    @PutMapping
    @RequiresAuthentication
    public ResultData<Void> updateDiscussion(@RequestBody DiscussionVO discussionVO) {
        boolean isOk = frontDiscussionService.updateDiscussion(discussionVO);
        if (!isOk) {
            return ResultData.fail("修改失败");
        }
        return ResultData.success("修改成功");
    }


    /**
     * @des: 点赞贴子
     *
     * @param did
     * @param toLike
     * @return
     */
    @PostMapping("discussion-like")
    @RequiresAuthentication
    public ResultData<Void> addDiscussionLike(@RequestParam(value = "did") Integer did,
                                              @RequestParam(value = "toLike") Boolean toLike) {
        boolean isOk = frontDiscussionService.addDiscussionLike(did, toLike);
        if (!isOk) {
            return ResultData.fail("操作失败");
        }
        return ResultData.success("操作成功");
    }


    /**
     * @des: 举报贴子
     *
     * @param discussionReport
     * @return
     */
    @PostMapping("discussion-report")
    @RequiresAuthentication
    public ResultData<Void> addDiscussionReport(@RequestBody DiscussionReport discussionReport) {
        boolean isOk = frontDiscussionService.addDiscussionReport(discussionReport);
        if (!isOk) {
            return ResultData.fail("举报失败！");
        }
        return ResultData.success("举报成功");
    }
}
