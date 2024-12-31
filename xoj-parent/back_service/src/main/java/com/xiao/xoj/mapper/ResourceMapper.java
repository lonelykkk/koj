package com.xiao.xoj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.entity.resource.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 肖恩
 * @since 2023-06-11
 */
public interface ResourceMapper extends BaseMapper<Resource> {

    IPage<Resource> getResourceList(IPage<Resource> page, @Param("isAdmin") boolean isAdmin);
}
