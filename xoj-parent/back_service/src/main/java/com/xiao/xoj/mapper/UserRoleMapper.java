package com.xiao.xoj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.pojo.entity.user.Role;
import com.xiao.xoj.pojo.entity.user.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.xoj.pojo.vo.UserRolesVO;
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
public interface UserRoleMapper extends BaseMapper<UserRole> {

    List<Role> getRolesByUid(@Param("uid") String uid);

    UserRolesVO getUserRoles(@Param("uid")String userId, @Param("username")String username);

    IPage<UserRolesVO> getUserList(Page<UserRolesVO> page,
                                   @Param("keyword") String keyword);

    IPage<UserRolesVO> getAdminUserList(Page<UserRolesVO> page,
                                        @Param("keyword") String keyword);
}
