package com.xiao.xoj.mapper;

import com.xiao.xoj.pojo.entity.user.Permission;
import com.xiao.xoj.pojo.entity.user.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    List<Permission> getAuthsByRid(@Param("rid")Integer rid);
}
