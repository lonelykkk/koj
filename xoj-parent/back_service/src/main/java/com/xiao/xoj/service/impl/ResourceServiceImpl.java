package com.xiao.xoj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.mapper.ResourceTagMapper;
import com.xiao.xoj.pojo.entity.resource.Resource;
import com.xiao.xoj.mapper.ResourceMapper;
import com.xiao.xoj.pojo.entity.resource.ResourceTag;
import com.xiao.xoj.service.ResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-06-11
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    @javax.annotation.Resource
    private ResourceMapper resourceMapper;

    @javax.annotation.Resource
    private ResourceTagMapper resourceTagMapper;

    @Override
    public IPage<Resource> getResourceList(Integer limit, Integer currentPage, String keyword) {

        if (limit == null || limit < 1) limit = 20;
        if (currentPage == null || currentPage < 1) currentPage = 1;
        IPage<Resource> page = new Page<>(currentPage, limit);

        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin")
                || SecurityUtils.getSubject().hasRole("root");

        return resourceMapper.getResourceList(page, isAdmin);
    }


    @Override
    public boolean addResource(Resource resource) {

        // 参数校验
        resource.setName(resource.getName().trim());
        resource.setDescription(resource.getDescription().trim());
        if (StringUtils.isEmpty(resource.getName())) {
            throw new StatusFailException("资源名称不能为空！");
        }
        if (StringUtils.isEmpty(resource.getDescription())) {
            throw new StatusFailException("资源描述不能为空！");
        }
        if (StringUtils.isEmpty(resource.getImg())) {
            throw new StatusFailException("资源封面不能为空！");
        }
        if (StringUtils.isEmpty(resource.getTarget())) {
            throw new StatusFailException("资源地址不能为空！");
        }
        if (resource.getRtId() == null) {
            throw new StatusFailException("请选择资源标签！");
        }

        // 判断资源标签是否存在
        if (resourceTagMapper.selectById(resource.getRtId()) == null) {
            throw new StatusFailException("资源标签不存在！请重新选择！");
        }

        return this.save(resource);
    }


    @Override
    public boolean updateResource(Resource resource) {

        // 参数校验
        resource.setName(resource.getName().trim());
        resource.setDescription(resource.getDescription().trim());
        if (StringUtils.isEmpty(resource.getName())) {
            throw new StatusFailException("资源名称不能为空！");
        }
        if (StringUtils.isEmpty(resource.getDescription())) {
            throw new StatusFailException("资源描述不能为空！");
        }
        if (StringUtils.isEmpty(resource.getImg())) {
            throw new StatusFailException("资源封面不能为空！");
        }
        if (StringUtils.isEmpty(resource.getTarget())) {
            throw new StatusFailException("资源地址不能为空！");
        }
        if (resource.getRtId() == null) {
            throw new StatusFailException("请选择资源标签！");
        }

        // 判断资源标签是否存在
        if (resourceTagMapper.selectById(resource.getRtId()) == null) {
            throw new StatusFailException("资源标签不存在！请重新选择！");
        }

        return this.updateById(resource);
    }


    @Override
    public boolean deleteResource(Integer rid) {
        Resource resource = this.getById(rid);
        if (resource == null) {
            throw new StatusFailException("删除失败！该资源不存在！");
        }
        return this.removeById(rid);
    }

}
