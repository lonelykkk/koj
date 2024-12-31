package com.xiao.xoj.controller;

import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.discussion.Category;
import com.xiao.xoj.service.CategoryService;
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
 * @description: 贴子分类控制器
 */
@RestController
@RequestMapping("/xoj/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * @des: 获取贴子分类
     *
     * @return
     */
    @GetMapping("get-category-list")
    public ResultData<List<Category>> getCategoryList() {
        List<Category> result = categoryService.getCategoryList();
        return ResultData.success(result);
    }


    /**
     * @des: 添加贴子分类
     *
     * @param category
     * @return
     */
    @PostMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> addCategory(@RequestBody Category category) {
        boolean isOk = categoryService.addCategory(category);
        if (!isOk) {
            return ResultData.fail("添加失败");
        }
        return ResultData.success("添加成功");
    }


    /**
     * @des： 修改贴子分类
     *
     * @param category
     * @return
     */
    @PutMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> updateCategory(@RequestBody Category category) {
        boolean isOk = categoryService.updateCategory(category);
        if (!isOk) {
            return ResultData.fail("修改失败");
        }
        return ResultData.success("修改成功");
    }


    /**
     * @des: 删除贴子分类
     *
     * @param cid
     * @return
     */
    @DeleteMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> deleteCategory(@RequestParam("cid") Integer cid) {
        boolean isOk = categoryService.deleteCategory(cid);
        if (!isOk) {
            return ResultData.fail("删除失败");
        }
        return ResultData.success("删除成功");
    }
}

