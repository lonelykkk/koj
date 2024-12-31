package com.xiao.xoj.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.discussion.Discussion;
import com.xiao.xoj.pojo.entity.discussion.DiscussionReport;
import com.xiao.xoj.pojo.vo.DiscussionReportVO;
import com.xiao.xoj.service.DiscussionService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-24
 * @dsc: 贴子讨论相关控制器
 */
@RestController
@RequestMapping("/xoj/discussion")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    /**
     * @des: 删除贴子，贴子下面的评论及评论回复也需要删除
     *
     * @param didList
     * @return
     */
    @DeleteMapping()
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> deleteDiscussion(@RequestBody List<Integer> didList) {
        boolean isOk = discussionService.deleteDiscussion(didList);
        if (!isOk) {
            return ResultData.fail("删除失败！");
        }
        return ResultData.success("删除成功！");
    }


    /**
     * @des： 修改贴子
     *
     * @param discussion
     * @return
     */
    @PutMapping()
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> updateDiscussion(@RequestBody Discussion discussion) {
        boolean isOk = discussionService.updateDiscussion(discussion);
        if (!isOk) {
            return ResultData.fail("修改失败！");
        }
        return ResultData.success("修改成功！");
    }


    /**
     * @des: 获取举报贴子信息
     *
     * @param limit
     * @param currentPage
     * @return
     */
    @GetMapping("get-report-list")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<IPage<DiscussionReportVO>> getReportList(@RequestParam(value = "limit", required = false) Integer limit,
                                                               @RequestParam(value = "currentPage", required = false) Integer currentPage) {
        IPage<DiscussionReportVO> page = discussionService.getReportList(limit, currentPage);
        return ResultData.success(page);
    }


    /**
     * @des:  修改贴子回复
     *
     * @param discussionReport
     * @return
     */
    @PutMapping("/discussion-report")
    @RequiresRoles(value = {"root", "admin","problem_admin"}, logical = Logical.OR)
    @RequiresAuthentication
    public ResultData<Void> updateDiscussionReport(@RequestBody DiscussionReport discussionReport) {
        boolean isOk =  discussionService.updateDiscussionReport(discussionReport);
        if (!isOk) {
            return ResultData.fail("修改失败！");
        }
        return ResultData.success("修改成功！");
    }

}

