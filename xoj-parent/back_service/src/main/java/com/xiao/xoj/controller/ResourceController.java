package com.xiao.xoj.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.resource.Resource;
import com.xiao.xoj.pojo.entity.resource.ResourceTag;
import com.xiao.xoj.pojo.vo.ResourceVO;
import com.xiao.xoj.service.ResourceService;
import com.xiao.xoj.service.ResourceTagService;
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
 * @since 2023-06-11
 */
@RestController
@RequestMapping("/xoj/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceTagService resourceTagService;

    /**
     * @des： 获取资源列表
     *
     * @param limit
     * @param currentPage
     * @param keyword
     * @return
     */
    @GetMapping("get-resource-list")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<IPage<Resource>> getResourceList(@RequestParam(value = "limit", required = false) Integer limit,
                                                       @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                       @RequestParam(value = "keyword", required = false) String keyword){

        IPage<Resource> page = resourceService.getResourceList(limit, currentPage, keyword);
        return ResultData.success(page);
    }


    /**
     * @des： 添加资源
     *
     * @param resource
     * @return
     */
    @PostMapping()
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> addResource(@RequestBody Resource resource) {
        boolean isOk = resourceService.addResource(resource);
        if (!isOk) {
            return ResultData.fail("添加失败！");
        }
        return ResultData.success("添加成功！");
    }


    /**
     * @des: 修改资源
     *
     * @param resource
     * @return
     */
    @PutMapping()
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> updateResource(@RequestBody Resource resource) {
        boolean isOk = resourceService.updateResource(resource);
        if (!isOk) {
            return ResultData.fail("修改失败！");
        }
        return ResultData.success("修改成功！");
    }


    /**
     * @des: 删除资源
     *
     * @param rid
     * @return
     */
    @DeleteMapping()
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> deleteResource(@RequestParam(value = "rid") Integer rid) {
        boolean isOk = resourceService.deleteResource(rid);
        if (!isOk) {
            return ResultData.fail("删除失败！");
        }
        return ResultData.success("删除成功！");
    }


    /**
     * @des: 获取资源分类列表
     *
     * @return
     */
    @GetMapping("get-tag-list")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<List<ResourceTag>> getTagList() {

        List<ResourceTag> list = resourceTagService.getTagList();
        return ResultData.success(list);
    }


    /**
     * @des: 添加资源分类
     *
     * @param resourceTag
     * @return
     */
    @PostMapping("add-tag")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> addTag(@RequestBody ResourceTag resourceTag) {
        boolean isOk = resourceTagService.addResourceTag(resourceTag);
        if (!isOk) {
            return ResultData.fail("添加失败！");
        }
        return ResultData.success("添加成功！");
    }

    /**
     * @des: 修改资源分类
     *
     * @param resourceTag
     * @return
     */
    @PutMapping("update-tag")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> updateResourceTag(@RequestBody ResourceTag resourceTag) {
        boolean isOk = resourceTagService.updateResourceTag(resourceTag);
        if (!isOk) {
            return ResultData.fail("修改失败！");
        }
        return ResultData.success("修改成功！");
    }


    /**
     * @des： 删除资源分类
     *
     * @param resourceTagId
     * @return
     */
    @DeleteMapping("delete-tag")
    @RequiresAuthentication
    @RequiresRoles(value = {"admin", "root"}, logical = Logical.OR)
    public ResultData<Void> deleteResourceTag(@RequestParam(value = "resourceTagId") Integer resourceTagId) {
        boolean isOk = resourceTagService.deleteResourceTag(resourceTagId);
        if (!isOk) {
            return ResultData.fail("删除失败！");
        }
        return ResultData.success("删除成功！");
    }


}

