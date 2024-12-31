package com.xiao.xoj.service;

import com.xiao.xoj.pojo.entity.user.Role;
import com.xiao.xoj.pojo.entity.user.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.vo.UserRolesVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
public interface UserRoleService extends IService<UserRole> {

    List<Role> getRolesByUid(String userId);

    UserRolesVO getUserRoles(String userId, String username);

    void deleteCache(String uid, boolean b);
}
