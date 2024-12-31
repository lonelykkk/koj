package com.xiao.xoj.controller;


import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.problem.Tag;
import com.xiao.xoj.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author 肖恩
 * @since 2023-03-19
 * @description 题目标签管理
 */
@Api("题目标签管理")
@RestController
@RequestMapping("/xoj/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @ApiOperation(value = "添加题目标签")
    @PostMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Tag> addTag(@RequestBody Tag tag) {
        tagService.addTag(tag);
        return ResultData.success("添加成功");
    }


    @ApiOperation(value = "修改标签")
    @PutMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Tag> updateTag(@RequestBody Tag tag) {
        tagService.updateTag(tag);
        return ResultData.success("修改成功");
    }


    @ApiOperation(value = "删除标签")
    @DeleteMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> deleteTag(@RequestParam("tid") Integer tid) {
        tagService.deleteTag(tid);
        return ResultData.success("删除成功");
    }


    @ApiOperation(value = "查询所有标签")
    @GetMapping("all")
    public ResultData<List<Tag>> getAllTag() {
        List<Tag> tagList = tagService.getAllTag();
        return ResultData.success(tagList);
    }

}

