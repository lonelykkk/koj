package com.xiao.xoj.service.front;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.entity.about.StudioAwards;
import com.xiao.xoj.pojo.entity.about.StudioInfo;

import java.util.List;

public interface FrontAboutService {
    List<StudioInfo> getMemberList();

    IPage<StudioAwards> getAwardsList(Integer limit, Integer currentPage);
}
