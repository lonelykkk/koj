package com.xiao.xoj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.dto.AdminEditUserDTO;
import com.xiao.xoj.pojo.dto.LoginDTO;
import com.xiao.xoj.pojo.entity.user.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.xoj.pojo.vo.UserInfoVO;
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
public interface UserInfoService extends IService<UserInfo> {

    UserInfoVO login(LoginDTO loginDTO);

    void logout();

    IPage<UserRolesVO> getUserList(Integer limit, Integer currentPage, Boolean onlyAdmin, String keyword);

    boolean editUser(AdminEditUserDTO adminEditUserDTO);

//    boolean editUserRole(String userId, Integer rid);

    boolean deleteUser(List<String> deleteUserIdList);
}
