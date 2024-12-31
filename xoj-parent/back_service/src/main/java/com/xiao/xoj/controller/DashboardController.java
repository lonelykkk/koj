package com.xiao.xoj.controller;

import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.vo.SystemInfoVO;
import com.xiao.xoj.service.SystemService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 肖恩
 * @create 2023/8/28 20:01
 * @description: 平台运行信息相关接口
 */
@RestController
@RequestMapping("/xoj/system")
public class DashboardController {

    @Autowired
    private SystemService systemService;

    @GetMapping("/get-system-info")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<SystemInfoVO> getSystemInfo() {
        SystemInfoVO systemInfoVO = systemService.getSystemInfo();
        if (systemInfoVO == null) {
            return ResultData.fail("获取失败");
        }
        return ResultData.success(systemInfoVO);
    }

}
