package com.xiao.xoj.service.front;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.pojo.dto.*;
import com.xiao.xoj.pojo.vo.*;

public interface FrontUserService {

    UserInfoVO login(LoginDTO loginDTO);

    RegisterCodeVO getRegisterCode(String email);

    boolean register(RegisterDTO registerDto);

    void logout();

    UserInfoVO getUserInfo();

    CheckUsernameOrEmailVO checkUsernameOrEmail(CheckUsernameOrEmailDTO checkUsernameOrEmailDTO);

    UserInfoVO changeUserInfo(UserInfoVO userInfoVO);

    String changePassword(ChangePasswordDTO changePasswordDTO);

    ChangeCodeVO getChangeCode(String email);

    UserInfoVO changeEmail(ChangeEmailDTO changeEmailDTO);

    IPage<UserContestInfoVO> getContestList(Integer limit, Integer currentPage);
}
