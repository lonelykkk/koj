package com.xiao.xoj.controller;


import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.wxqf.Prompt;
import com.xiao.xoj.service.PromptService;
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
 * @since 2023-08-05
 */
@RestController
@RequestMapping("/xoj/prompt")
public class PromptController {

    @Autowired
    private PromptService promptService;

    /**
     * @des: 获取模板列表
     *
     * @return
     */
    @GetMapping
    public ResultData<List<Prompt>> getPromptList() {
        List<Prompt> prompts = promptService.getPromptList();
        return ResultData.success(prompts);
    }


    /**
     * @des: 添加模板
     *
     * @param prompt
     * @return
     */
    @PostMapping
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> addPrompt(@RequestBody Prompt prompt) {
        boolean isOk = promptService.addPrompt(prompt);
        if (!isOk) {
            return ResultData.fail("添加失败！");
        }
        return ResultData.success("添加成功！");
    }


    /**
     * @des: 修改模板信息
     *
     * @param prompt
     * @return
     */
    @PutMapping
    public ResultData<Void> updatePrompt(@RequestBody Prompt prompt) {
        boolean isOk = promptService.updatePrompt(prompt);
        if (!isOk) {
            return ResultData.fail("修改失败！");
        }
        return ResultData.success("修改成功");
    }


    /**
     * @des: 删除模板
     *
     * @param pid
     * @return
     */
    @DeleteMapping
    public ResultData<Void> deletePrompt(@RequestParam(value = "pid") Integer pid) {
        boolean isOk = promptService.deletePrompt(pid);
        if (!isOk) {
            return ResultData.fail("删除失败");
        }
        return ResultData.success("删除成功");
    }

}

