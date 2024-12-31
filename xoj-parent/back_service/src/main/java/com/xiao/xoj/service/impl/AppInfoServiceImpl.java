package com.xiao.xoj.service.impl;

import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.pojo.entity.wxqf.AppInfo;
import com.xiao.xoj.mapper.AppInfoMapper;
import com.xiao.xoj.service.AppInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-08-05
 */
@Service
public class AppInfoServiceImpl extends ServiceImpl<AppInfoMapper, AppInfo> implements AppInfoService {

    @Override
    public boolean addApp(AppInfo appInfo) {
        // 参数校验
        if (StringUtils.isEmpty(appInfo.getApiKey()) || StringUtils.isEmpty(appInfo.getSecretKey())) {
            throw new StatusFailException("api key或secret key不能为空");
        }
        if (StringUtils.isEmpty(appInfo.getTitle()) || StringUtils.isEmpty(appInfo.getDescription())) {
            throw new StatusFailException("应用标题或应用描述不能为空");
        }
        // 当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        appInfo.setUserId(accountProfile.getId());
        return this.save(appInfo);
    }

    @Override
    public List<AppInfo> getAppList() {
        return this.list(null);
    }

    @Override
    public boolean updateApp(AppInfo appInfo) {
        // 参数校验
        if (StringUtils.isEmpty(appInfo.getApiKey()) || StringUtils.isEmpty(appInfo.getSecretKey())) {
            throw new StatusFailException("api key或secret key不能为空");
        }
        if (StringUtils.isEmpty(appInfo.getTitle()) || StringUtils.isEmpty(appInfo.getDescription())) {
            throw new StatusFailException("应用标题或应用描述不能为空");
        }
        // 当前用户
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        appInfo.setUserId(accountProfile.getId());
        return this.updateById(appInfo);
    }

    @Override
    public boolean deleteApp(Integer appId) {
        return this.removeById(appId);
    }
}
