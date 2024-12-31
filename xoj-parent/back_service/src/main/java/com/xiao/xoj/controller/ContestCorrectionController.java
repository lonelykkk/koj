package com.xiao.xoj.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.contest.ContestCorrection;
import com.xiao.xoj.pojo.vo.ContestCorrectionVO;
import com.xiao.xoj.pojo.vo.ContestStudentInfoVO;
import com.xiao.xoj.service.ContestCorrectionService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 肖恩
 * @since 2023-07-14
 */
@RestController
@RequestMapping("/xoj/contest-correction")
public class ContestCorrectionController {

    @Resource
    private ContestCorrectionService contestCorrectionService;

    /**
     * @des: 获取参加考核的新生列表
     *
     * @param limit
     * @param currentPage
     * @param keyword
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @GetMapping("get-student-info")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<IPage<ContestStudentInfoVO>> getContestStudentInfo(@RequestParam(value = "limit", required = false) Integer limit,
                                                                         @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                         @RequestParam(value = "keyword", required = false) String keyword,
                                                                         @RequestParam(value = "cid") Integer cid) {
        IPage<ContestStudentInfoVO> page = contestCorrectionService.getContestStudentInfo(limit, currentPage, keyword, cid);
        return ResultData.success(page);
    }


    /**
     * @des: 添加批改信息
     *
     * @param contestCorrection
     * @return
     */
    @PostMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> addContestCorrection(@RequestBody ContestCorrection contestCorrection) {
        boolean isOk = contestCorrectionService.addContestCorrection(contestCorrection);
        if (!isOk) {
            return ResultData.fail("添加失败!");
        }
        return ResultData.success("添加成功！");
    }


    /**
     * @des: 获取批改信息
     *
     * @param cid
     * @param userId
     * @return
     */
    @GetMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<ContestCorrectionVO> getContestCorrectionInfo(@RequestParam(value = "contestId") Integer cid,
                                                                    @RequestParam(value = "userId") String userId) {
        ContestCorrectionVO contestCorrectionVO = contestCorrectionService.getContestCorrectionInfo(cid, userId);
        return ResultData.success(contestCorrectionVO);
    }


    /**
     * @des： 修改批改信息
     *
     * @param contestCorrection
     * @return
     */
    @PutMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> updateContestCorrection(@RequestBody ContestCorrection contestCorrection) {
        boolean isOk = contestCorrectionService.updateContestCorrection(contestCorrection);
        if (!isOk) {
            return ResultData.fail("修改失败!");
        }
        return ResultData.success("修改成功！");
    }


    /**
     * @des: 删除批改信息
     *
     * @param contestCorrectionId
     * @return
     */
    @DeleteMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> deleteContestCorrectionInfo(@RequestParam(value = "contestCorrectionId") Integer contestCorrectionId) {
        boolean isOk = contestCorrectionService.deleteContestCorrectionInfo(contestCorrectionId);
        if (!isOk) {
            return ResultData.fail("修改失败!");
        }
        return ResultData.success("修改成功！");
    }


    /**
     * @des: 统一发送考核批改邮件，只有超级管理员有权限操作
     *
     * @param contestId  考核ID
     * @return
     */
    @GetMapping("/send-correction-email")
    @RequiresAuthentication
    @RequiresRoles(value = {"root"}, logical = Logical.AND)
    public ResultData<Void> sendCorrectionEmail(@RequestParam(value = "contestId") Integer contestId) {
        boolean isOk = contestCorrectionService.sendCorrectionEmail(contestId);
        if (!isOk) {
            return ResultData.fail("发送失败");
        }
        return ResultData.success("发送成功");
    }
}

