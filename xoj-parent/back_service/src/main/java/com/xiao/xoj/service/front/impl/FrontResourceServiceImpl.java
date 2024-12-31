package com.xiao.xoj.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiao.xoj.pojo.entity.resource.Resource;
import com.xiao.xoj.pojo.entity.resource.ResourceTag;
import com.xiao.xoj.pojo.vo.ResourceVO;
import com.xiao.xoj.service.ResourceService;
import com.xiao.xoj.service.ResourceTagService;
import com.xiao.xoj.service.front.FrontResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/6/11 10:56
 * @description: TODO
 */
@Service
public class FrontResourceServiceImpl implements FrontResourceService {

    @Autowired
    private ResourceTagService resourceTagService;

    @Autowired
    private ResourceService resourceService;


    @Override
    public List<ResourceVO> getResourceList(String keyword, Integer resourceTagId) {
        List<ResourceVO> res = new ArrayList<>();
        if (resourceTagId != null && resourceTagId != 0) {
            ResourceVO resourceVO = new ResourceVO();
            ResourceTag resourceTag = resourceTagService.getById(resourceTagId);
            if (resourceTag == null) {
                return null;
            }
            QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("rt_id", resourceTagId).eq("visible", 0);
            List<Resource> resourceList = resourceService.list(queryWrapper);
            if (resourceList.size() != 0) {
                resourceVO.setResourceTag(resourceTag);
                resourceVO.setResourceList(resourceList);
                res.add(resourceVO);
            }
        } else {
            // todo 写一个SQL语句
            List<ResourceTag> tags = resourceTagService.list(null);
            for (int i = 0; i < tags.size(); i ++) {
                ResourceVO resourceVO = new ResourceVO();
                ResourceTag tag = tags.get(i);
                QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("rt_id", tag.getId()).eq("visible", 0);
                List<Resource> resourceList = resourceService.list(queryWrapper);
                if (resourceList.size() != 0) {
                    resourceVO.setResourceTag(tag);
                    resourceVO.setResourceList(resourceList);
                    res.add(resourceVO);
                }
            }
        }
        return res;
    }


    @Override
    public List<ResourceTag> getResourceTag() {
        return resourceTagService.list(null);
    }
}
