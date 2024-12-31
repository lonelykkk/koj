package com.xiao.xoj.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.dto.ProblemDto;
import com.xiao.xoj.pojo.entity.problem.Problem;
import com.xiao.xoj.pojo.entity.problem.Tag;
import com.xiao.xoj.service.ProblemDescriptionService;
import com.xiao.xoj.service.ProblemService;
import com.xiao.xoj.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
@Api("题目管理")
@RestController
@RequestMapping("/xoj/problem")
public class ProblemController {
    /**
     *
     * 先输入题目基本信息，题目描述
     * 再输入测试用例：手动 or 上传zip
     * 其他，如语言，标签，代码模板等等
    */


    @Autowired
    private ProblemService problemService;

    @Autowired
    private ProblemDescriptionService problemDescriptionService;

    @Autowired
    private TagService tagService;

    @ApiOperation(value = "添加题目")
    @PostMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> addProblem(@RequestBody ProblemDto problemDto) {
        boolean isOk = problemService.addProblem(problemDto);
        return ResultData.success("添加成功");
    }


    // todo 测试数据的更新如何解决
    @ApiOperation(value = "修改题目")
    @PutMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> updateProblem(@RequestBody ProblemDto problemDto) {
        boolean isOk = problemService.updateProblem(problemDto);
        return ResultData.success("修改成功");
    }


    @ApiOperation(value = "删除题目")
    @DeleteMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> deleteProblem(@RequestParam("pid") Integer pid) {
        boolean isOk = problemService.deleteProblem(pid);
        return ResultData.success("删除成功");
    }


    @ApiOperation(value = "获取所有题目")
    @GetMapping("getAll")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<IPage<Problem>> getAllProblem(@RequestParam(value = "limit", required = false) Integer limit,
                                                    @RequestParam(value = "currentPage", required = false) Integer currPage,
                                                    @RequestParam(value = "keyword", required = false) String keyword,
                                                    @RequestParam(value = "auth", required = false) Integer auth,
                                                    @RequestParam(value = "contestId", required = false) Integer cid) {
        IPage<Problem> problems = problemService.getAllProblem(limit, currPage, keyword, auth, cid);
        return ResultData.success(problems);
    }


    @ApiOperation(value = "根据题目id获取题目信息")
    @GetMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Map<String, Object>> getProblem(@RequestParam("pid") Integer pid) {
        Problem problem = problemService.getProblem(pid);
        String description = problemDescriptionService.getById(pid).getDescription();
        List<Tag> pTags = tagService.getTagByPid(pid);
        Map<String, Object> map = new HashMap<>();
        map.put("problem", problem);
        map.put("description", description);
        map.put("tags", pTags);
        return ResultData.success(map);
    }


}

