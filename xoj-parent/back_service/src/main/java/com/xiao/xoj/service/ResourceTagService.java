package com.xiao.xoj.service;

import com.xiao.xoj.pojo.entity.resource.ResourceTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-06-11
 */
public interface ResourceTagService extends IService<ResourceTag> {

    List<ResourceTag> getTagList();

    boolean addResourceTag(ResourceTag resourceTag);

    boolean updateResourceTag(ResourceTag resourceTag);

    boolean deleteResourceTag(Integer resourceTagId);
}
