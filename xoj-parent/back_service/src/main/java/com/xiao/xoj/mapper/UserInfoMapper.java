package com.xiao.xoj.mapper;

import com.xiao.xoj.pojo.dto.RegisterDTO;
import com.xiao.xoj.pojo.entity.user.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    boolean addUser(RegisterDTO registerDto);
}
