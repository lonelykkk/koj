package com.xiao.xoj.service.front;

import com.xiao.xoj.pojo.entity.resource.ResourceTag;
import com.xiao.xoj.pojo.vo.ResourceVO;

import java.util.List;

public interface FrontResourceService {
    List<ResourceVO> getResourceList(String keyword, Integer resourceTagId);

    List<ResourceTag> getResourceTag();
}
