package com.xiao.xoj.controller;

import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.entity.wxqf.AppInfo;
import com.xiao.xoj.service.AppInfoService;
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
@RequestMapping("/xoj/app-info")
public class AppInfoController {

    @Autowired
    private AppInfoService appInfoService;

    /**
     * @des: 添加应用
     *
     * @param appInfo
     * @return
     */
    @PostMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> addApp(@RequestBody AppInfo appInfo) {
        boolean isOk = appInfoService.addApp(appInfo);
        if (!isOk) {
            return ResultData.fail("添加失败");
        }

        return ResultData.success("添加成功");
    }

    /**
     * @des: 获取应用列表
     *
     * @return
     */
    @GetMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<List<AppInfo>> getAppList() {
        List<AppInfo> appList = appInfoService.getAppList();
        return ResultData.success(appList);
    }


    /**
     * @des: 修改应用信息
     *
     * @param appInfo
     * @return
     */
    @PutMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> updateApp(@RequestBody AppInfo appInfo) {
        boolean isOk = appInfoService.updateApp(appInfo);
        if (!isOk) {
            return ResultData.fail("修改失败");
        }
        return ResultData.success("修改成功");
    }

    /**
     * @des: 删除应用
     *
     * @param appId
     * @return
     */
    @DeleteMapping
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> deleteApp(@RequestParam(value = "id") Integer appId) {
        boolean isOk = appInfoService.deleteApp(appId);
        if (!isOk) {
            return ResultData.fail("删除失败");
        }
        return ResultData.success("删除成功");
    }
}

