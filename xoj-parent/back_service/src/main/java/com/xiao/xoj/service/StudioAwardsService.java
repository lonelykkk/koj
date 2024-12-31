package com.xiao.xoj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.entity.about.StudioAwards;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-06-11
 */
public interface StudioAwardsService extends IService<StudioAwards> {

    IPage<StudioAwards> getAwardsList(Integer limit, Integer currentPage, String keyword);

    boolean addAwards(StudioAwards studioAwards);

    boolean updateAwards(StudioAwards studioAwards);

    boolean deleteAwards(Integer aid);
}
