package com.xiao.xoj.service;

import com.xiao.xoj.pojo.entity.wxqf.AppInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-08-05
 */
public interface AppInfoService extends IService<AppInfo> {

    boolean addApp(AppInfo appInfo);

    List<AppInfo> getAppList();

    boolean updateApp(AppInfo appInfo);

    boolean deleteApp(Integer appId);
}
