package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.pojo.entity.resource.ResourceTag;
import com.xiao.xoj.mapper.ResourceTagMapper;
import com.xiao.xoj.service.ResourceTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-06-11
 */
@Service
public class ResourceTagServiceImpl extends ServiceImpl<ResourceTagMapper, ResourceTag> implements ResourceTagService {


    @Override
    public List<ResourceTag> getTagList() {
        return this.list(null);
    }

    @Override
    public boolean addResourceTag(ResourceTag resourceTag) {

        resourceTag.setName(resourceTag.getName().trim());
        if (StringUtils.isEmpty(resourceTag.getName())) {
            throw new StatusFailException("添加失败！资源标签名称不能为空！");
        }

        // 查询该标签是否已经存在
        QueryWrapper<ResourceTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", resourceTag.getName());
        ResourceTag isExit = this.getOne(queryWrapper);
        if (isExit != null) {
            throw new StatusFailException("添加失败！该资源标签名称已经存在！请勿重复添加！");
        }

        return this.save(resourceTag);
    }


    @Override
    public boolean updateResourceTag(ResourceTag resourceTag) {

        // 参数校验
        resourceTag.setName(resourceTag.getName().trim());
        if (StringUtils.isEmpty(resourceTag.getName())) {
            throw new StatusFailException("修改失败！资源标签名称不能为空！");
        }
        if (resourceTag.getId() == null) {
            throw new StatusFailException("修改失败！资源标签ID不能为空！");
        }
        if (this.getById(resourceTag.getId()) == null) {
            throw new StatusFailException("修改失败！请刷新重试！");
        }

        // 查询该标签是否已经存在
        QueryWrapper<ResourceTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", resourceTag.getName());
        ResourceTag isExit = this.getOne(queryWrapper);
        if (isExit != null) {
            throw new StatusFailException("修改失败！该资源标签名称已经存在！请勿重复添加！");
        }

        return this.updateById(resourceTag);
    }

    @Override
    public boolean deleteResourceTag(Integer resourceTagId) {

        ResourceTag resourceTag = this.getById(resourceTagId);
        if (resourceTag == null) {
            throw new StatusFailException("删除失败！该资源标签不存在！");
        }

        return this.removeById(resourceTag.getId());
    }
}
