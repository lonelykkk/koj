package com.xiao.xoj.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.dto.AnnouncementDTO;
import com.xiao.xoj.pojo.dto.ContestProblemDTO;
import com.xiao.xoj.pojo.entity.contest.Contest;
import com.xiao.xoj.pojo.entity.contest.ContestProblem;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.vo.AnnouncementVO;
import com.xiao.xoj.service.ContestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 肖恩
 * @since 2023-05-30
 */
@Slf4j
@RestController
@RequestMapping("/xoj/contest")
public class ContestController {

    @Resource
    private ContestService contestService;


    /**
     * @des: 获取考核列表
     *
     * @param limit
     * @param currentPage
     * @param keyword
     * @return
     */
    @GetMapping("/get-contest-list")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<IPage<Contest>> getContestList(@RequestParam(value = "limit", required = false) Integer limit,
                                                      @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                      @RequestParam(value = "keyword", required = false) String keyword) {

        IPage<Contest> res = contestService.getContestList(limit, currentPage, keyword);
        return ResultData.success(res);
    }


    /**
     * @des: 获取考核详情
     *
     * @param cid
     * @return
     */
    @GetMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Contest> getContest(@RequestParam(value = "cid") Integer cid) {
        Contest contest = contestService.getContest(cid);
        return ResultData.success(contest);
    }


    /**
     * @des: 添加考核
     *
     * @param contest
     * @return
     */
    @PostMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> addContest(@RequestBody Contest contest) {
        boolean isOk = contestService.addContest(contest);
        if (!isOk) {
            return ResultData.fail("创建失败！请重试！");
        }
        return ResultData.success("创建成功！");
    }


    /**
     * @des： 删除考核
     *
     * @param cid
     * @return
     */
    @DeleteMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> deleteContest(@RequestParam(value = "cid") Integer cid) {
        boolean isOk = contestService.deleteContest(cid);
        if (!isOk) {
            return ResultData.fail("删除失败！请重试！");
        }
        return ResultData.success("删除成功！");
    }


    /**
     * @des: 修改考核
     *
     * @param contest
     * @return
     */
    @PutMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> updateContest(@RequestBody Contest contest) {
        boolean isOk = contestService.updateContest(contest);
        if (!isOk) {
            return ResultData.fail("修改失败！请重试！");
        }
        return ResultData.success("修改成功！");
    }


    // 以下为考核的题目的增删改查操作接口

    /**
     * @des: 获取考核题目列表
     *
     * @param limit
     * @param currentPage
     * @param keyword
     * @param cid
     * @return
     */
    @GetMapping("/get-problem-list")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<IPage<Problem>> getProblemList(@RequestParam(value = "limit", required = false) Integer limit,
                                                     @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                     @RequestParam(value = "keyword", required = false) String keyword,
                                                     @RequestParam(value = "cid", required = true) Long cid) {
        IPage<Problem> res = contestService.getProblemList(limit, currentPage, keyword, cid);
        return ResultData.success(res);
    }


    /**
     * @des: 添加考核题目，只能从公共题库中选取
     *
     * @param contestProblemDto
     * @return
     */
    @PostMapping("add-problem-from-public")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> addProblemFromPublic(@Valid @RequestBody ContestProblemDTO contestProblemDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            FieldError fieldError = (FieldError) errors.get(0);
            log.error("Invalid Parameter : object - {}, field - {}, errorMessage - {}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
            return ResultData.fail("Invalid Parameter: " + fieldError.getDefaultMessage());
        }

        boolean isOk = contestService.addProblemFromPublic(contestProblemDto);
        if (!isOk) {
            return ResultData.fail("添加失败！请重试！");
        }
        return ResultData.success("添加成功！");
    }

    /**
     * @des： 修改考核题目
     *
     * @param contestProblemDto
     * @param bindingResult
     * @return
     */
    @PutMapping("update-problem")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> updateProblem(@Valid @RequestBody ContestProblemDTO contestProblemDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            FieldError fieldError = (FieldError) errors.get(0);
            log.error("Invalid Parameter : object - {}, field - {}, errorMessage - {}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
            return ResultData.fail("Invalid Parameter: " + fieldError.getDefaultMessage());
        }

        boolean isOk = contestService.updateProblem(contestProblemDto);
        if (!isOk) {
            return ResultData.fail("修改失败！请重试！");
        }
        return ResultData.success("修改成功！");
    }


    /**
     * @des: 移除考核题目
     *
     * @param cid
     * @param pid
     * @return
     */
    @DeleteMapping("remove-problem")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> removeProblem(@RequestParam(value = "cid") Integer cid,
                                          @RequestParam(value = "pid") Integer pid) {
        boolean isOk = contestService.removeProblem(cid, pid);
        if (!isOk) {
            return ResultData.fail("移除失败！请重试！");
        }
        return ResultData.success("移除成功！");
    }


    // 以下处理比赛公告的操作请求

    /**
     * @des: 获取公告
     *
     * @param limit
     * @param currentPage
     * @param cid
     * @return
     */
    @GetMapping("/announcement")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<IPage<AnnouncementVO>> getAnnouncementList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                 @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                 @RequestParam(value = "cid", required = true) Integer cid) {

        IPage<AnnouncementVO> res = contestService.getAnnouncementList(limit, currentPage, cid);
        return ResultData.success(res);
    }


    /**
     * @des： 添加公告
     *
     * @param announcementDto
     * @return
     */
    @PostMapping("/announcement")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> addAnnouncement(@RequestBody AnnouncementDTO announcementDto) {
        boolean isOk = contestService.addAnnouncement(announcementDto);
        if (!isOk) {
            return ResultData.fail("添加失败！请重试！");
        }
        return ResultData.success("添加成功！");
    }


    /**
     * @des： 删除公告
     *
     * @param aid
     * @return
     */
    @DeleteMapping("/announcement")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> deleteAnnouncement(@RequestParam("aid") Integer aid) {
        boolean isOk = contestService.deleteAnnouncement(aid);
        if (!isOk) {
            return ResultData.fail("删除失败！请重试！");
        }
        return ResultData.success("删除成功！");
    }


    /**
     * @des： 修改公告
     *
     * @param announcementDto
     * @return
     */
    @PutMapping("/announcement")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> updateAnnouncement(@Valid @RequestBody AnnouncementDTO announcementDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            FieldError fieldError = (FieldError) errors.get(0);
            log.error("Invalid Parameter : object - {}, field - {}, errorMessage - {}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
            return ResultData.fail("Invalid Parameter: " + fieldError.getDefaultMessage());
        }

        boolean isOk = contestService.updateAnnouncement(announcementDto);
        if (!isOk) {
            return ResultData.fail("修改失败！请重试！");
        }
        return ResultData.success("修改成功！");
    }

}

