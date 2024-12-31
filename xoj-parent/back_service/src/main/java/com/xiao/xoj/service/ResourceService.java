package com.xiao.xoj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.entity.resource.Resource;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-06-11
 */
public interface ResourceService extends IService<Resource> {

    IPage<Resource> getResourceList(Integer limit, Integer currentPage, String keyword);

    boolean addResource(Resource resource);

    boolean updateResource(Resource resource);

    boolean deleteResource(Integer rid);
}
