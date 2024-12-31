package com.xiao.xoj.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.mapper.StudioAwardsMapper;
import com.xiao.xoj.pojo.entity.about.StudioAwards;
import com.xiao.xoj.pojo.entity.about.StudioInfo;
import com.xiao.xoj.service.StudioAwardsService;
import com.xiao.xoj.service.StudioInfoService;
import com.xiao.xoj.service.front.FrontAboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/6/11 10:56
 * @description: TODO
 */
@Service
public class FrontAboutServiceImpl implements FrontAboutService {

    @Autowired
    private StudioInfoService studioInfoService;

    @Resource
    private StudioAwardsMapper studioAwardsMapper;

    @Override
    public List<StudioInfo> getMemberList() {

        QueryWrapper<StudioInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_teacher", 0).orderByDesc("which_session");

        return studioInfoService.list(queryWrapper);
    }

    @Override
    public IPage<StudioAwards> getAwardsList(Integer limit, Integer currentPage) {

        if (limit == null || limit < 1) limit = 10;
        if (currentPage == null || currentPage < 1) currentPage = 1;
        IPage<StudioAwards> page = new Page<>(currentPage, limit);

        return studioAwardsMapper.getAwardsList(page, null);
    }
}
