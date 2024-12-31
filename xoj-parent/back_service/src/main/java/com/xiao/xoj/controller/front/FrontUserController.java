package com.xiao.xoj.controller.front;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.dto.*;
import com.xiao.xoj.pojo.vo.*;
import com.xiao.xoj.service.front.FrontUserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 肖恩
 * @create 2023/6/8 23:39
 * @description: TODO
 */
@RestController
@RequestMapping("/xoj/front-user")
public class FrontUserController {

    @Autowired
    private FrontUserService frontUserService;


    /**
     * @des: 用户登录
     *
     * @param loginDTO
     * @return
     */
    @PostMapping("login")
    public ResultData<UserInfoVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        UserInfoVO userInfoVO = frontUserService.login(loginDTO);
        return ResultData.success(userInfoVO);
    }


    /**
     * @des: 注册时 获取6位验证码
     *
     * @param email
     * @return
     */
    @GetMapping("get-register-code")
    public ResultData<RegisterCodeVO> getRegisterCode(@RequestParam("email") String email) {
        RegisterCodeVO registerCodeVO = frontUserService.getRegisterCode(email);
        return ResultData.success(registerCodeVO);
    }


    /**
     * @des: 用户注册
     *
     * @param registerDto
     * @return
     */
    @PostMapping("register")
    public ResultData<Void> register(@Valid @RequestBody RegisterDTO registerDto) {
        boolean isOk = frontUserService.register(registerDto);
        if (!isOk) {
            return ResultData.fail("注册失败！");
        }
        return ResultData.success("注册成功！");
    }


    /**
     * @des: 用户退出登录
     *
     * @return
     */
    @GetMapping("/logout")
    @RequiresAuthentication
    public ResultData<Void> logout() {
        frontUserService.logout();
        return ResultData.success();
    }


    /**
     * @des: 获取用户信息
     *
     * @return
     */
    @GetMapping("/get-user-info")
    @RequiresAuthentication
    public ResultData<UserInfoVO> getUserInfo() {
        UserInfoVO userInfoVO = frontUserService.getUserInfo();
        return ResultData.success(userInfoVO);
    }


    /**
     * @des: 检验用户名和邮箱是否存在
     *
     * @param checkUsernameOrEmailDTO
     * @return
     */
    @PostMapping("/check-username-or-email")
    public ResultData<CheckUsernameOrEmailVO> checkUsernameOrEmail(@RequestBody CheckUsernameOrEmailDTO checkUsernameOrEmailDTO) {

        return ResultData.success(frontUserService.checkUsernameOrEmail(checkUsernameOrEmailDTO));
    }


    /**
     * @des： 用户修改用户信息
     *
     * @param userInfoVO
     * @return
     */
    @PutMapping("/change-userInfo")
    @RequiresAuthentication
    public ResultData<UserInfoVO> changeUserInfo(@Valid @RequestBody UserInfoVO userInfoVO) {
        UserInfoVO userInfo = frontUserService.changeUserInfo(userInfoVO);
        return ResultData.success(userInfo);
    }

    // todo 修改用户头像

    /**
     * @des: 用户修改密码
     *
     * @param changePasswordDTO
     * @return
     */
    @PostMapping("change-password")
    @RequiresAuthentication
    public ResultData<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        String msg = frontUserService.changePassword(changePasswordDTO);
        return ResultData.success(msg,"修改成功");
    }


    /**
     * @des: 获取更改邮箱时新邮箱的验证码
     *
     * @param email
     * @return
     */
    @GetMapping("get-change-code")
    @RequiresAuthentication
    public ResultData<ChangeCodeVO> getChangeCode(@RequestParam("email") String email) {
        ChangeCodeVO changeCodeVO = frontUserService.getChangeCode(email);
        return ResultData.success(changeCodeVO);
    }


    /**
     * @des： 修改邮箱
     *
     * @param changeEmailDTO
     * @return
     */
    @PostMapping("change-email")
    @RequiresAuthentication
    public ResultData<UserInfoVO> changeEmail(@RequestBody ChangeEmailDTO changeEmailDTO) {
        UserInfoVO userInfoVO = frontUserService.changeEmail(changeEmailDTO);
        return ResultData.success(userInfoVO);
    }


    // todo 获取做题记录


    /**
     * @des: 获取考核记录
     *
     * @param limit
     * @param currentPage
     * @return
     */
    @GetMapping("get-contest-list")
    @RequiresAuthentication
    public ResultData<IPage<UserContestInfoVO>> getContestList(@RequestParam(value = "limit", required = false) Integer limit,
                                                               @RequestParam(value = "currentPage", required = false) Integer currentPage) {

        IPage<UserContestInfoVO> page = frontUserService.getContestList(limit, currentPage);
        return ResultData.success(page);
    }


}
