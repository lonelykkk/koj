package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.pojo.entity.about.StudioAwards;
import com.xiao.xoj.mapper.StudioAwardsMapper;
import com.xiao.xoj.service.StudioAwardsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-06-11
 */
@Service
public class StudioAwardsServiceImpl extends ServiceImpl<StudioAwardsMapper, StudioAwards> implements StudioAwardsService {

    @Resource
    private StudioAwardsMapper studioAwardsMapper;

    @Override
    public IPage<StudioAwards> getAwardsList(Integer limit, Integer currentPage, String keyword) {
        if (limit == null || limit < 1) limit = 10;
        if (currentPage == null || currentPage < 1) currentPage = 1;
        IPage<StudioAwards> page = new Page<>(currentPage, limit);
        return studioAwardsMapper.getAwardsList(page, keyword);
    }


    @Override
    public boolean addAwards(StudioAwards studioAwards) {
        // 参数校验
        if (StringUtils.isEmpty(studioAwards.getName().trim())) {
            throw new StatusFailException("奖项名称不能为空！");
        }
        if (StringUtils.isEmpty(studioAwards.getAuthor().trim())) {
            throw new StatusFailException("作者不能为空！");
        }
        if (studioAwards.getGetTime() == null) {
            throw new StatusFailException("获奖时间不能为空！");
        }

        return this.save(studioAwards);
    }

    @Override
    public boolean updateAwards(StudioAwards studioAwards) {
        // 参数校验
        if (StringUtils.isEmpty(studioAwards.getName().trim())) {
            throw new StatusFailException("奖项名称不能为空！");
        }
        if (StringUtils.isEmpty(studioAwards.getAuthor().trim())) {
            throw new StatusFailException("作者不能为空！");
        }
        if (studioAwards.getGetTime() == null) {
            throw new StatusFailException("获奖时间不能为空！");
        }
        if (studioAwards.getId() == null || this.getById(studioAwards.getId()) == null) {
            throw new StatusFailException("更新失败！请刷新重试！");
        }

        return this.saveOrUpdate(studioAwards);
    }

    @Override
    public boolean deleteAwards(Integer aid) {
        StudioAwards studioAwards = this.getById(aid);
        if (studioAwards == null) {
            throw new StatusFailException("删除失败！该奖项不存在！");
        }
        return this.removeById(aid);
    }

}
