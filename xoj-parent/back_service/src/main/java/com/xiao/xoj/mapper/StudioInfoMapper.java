package com.xiao.xoj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.entity.about.StudioInfo;
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
public interface StudioInfoMapper extends BaseMapper<StudioInfo> {

    IPage<StudioInfo> getMemberList(IPage<StudioInfo> page, @Param("keyword")String keyword);
}
