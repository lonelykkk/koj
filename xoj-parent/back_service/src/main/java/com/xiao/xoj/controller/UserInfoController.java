package com.xiao.xoj.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.dto.AdminEditUserDTO;
import com.xiao.xoj.pojo.dto.LoginDTO;
import com.xiao.xoj.pojo.entity.user.UserInfo;
import com.xiao.xoj.pojo.vo.UserInfoVO;
import com.xiao.xoj.pojo.vo.UserRolesVO;
import com.xiao.xoj.service.UserInfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
@RestController
@RequestMapping("/xoj/user-info")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;


    /**
     * @des: 管理员登录
     *
     * @param loginDTO
     * @return
     */
    @PostMapping("login")
    public ResultData<UserInfoVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        UserInfoVO userInfo = userInfoService.login(loginDTO);
        return ResultData.success(userInfo);
    }


    /**
     * @des: 管理员登出
     *
     * @return
     */
    @GetMapping("logout")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> logout() {
        userInfoService.logout();
        return ResultData.success();
    }


    /**
     * @des: 获取用户列表
     *
     * @param limit
     * @param currentPage
     * @param onlyAdmin
     * @param keyword
     * @return
     */
    @GetMapping("/get-user-list")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<IPage<UserRolesVO>> getUserList(@RequestParam(value = "limit", required = false) Integer limit,
                                                      @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                      @RequestParam(value = "onlyAdmin", defaultValue = "false") Boolean onlyAdmin,
                                                      @RequestParam(value = "keyword", required = false) String keyword) {

        IPage<UserRolesVO> userRolesVOIPage = userInfoService.getUserList(limit, currentPage, onlyAdmin, keyword);
        return ResultData.success(userRolesVOIPage);
    }


    /**
     * @des: 编辑用户信息
     *
     * @param adminEditUserDTO
     * @return
     */
    @PutMapping("/edit-user")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin"}, logical = Logical.OR)
    public ResultData<Void> editUser(@RequestBody AdminEditUserDTO adminEditUserDTO) {
        boolean isOk = userInfoService.editUser(adminEditUserDTO);
        if (!isOk) {
            return ResultData.fail("修改失败！请重试！");
        }
        return ResultData.success("修改成功！");
    }


//    /**
//     * @des: 修改用户角色，只有超级管理员有这个权限
//     *
//     * @param userId
//     * @param rid
//     * @return
//     */
//    @PutMapping("/edit-user-role")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root"})
//    public ResultData<Void> editUserRole(@RequestParam(value = "userId") String userId,
//                                         @RequestParam(value = "rid") Integer rid) {
//        boolean isOk = userInfoService.editUserRole(userId, rid);
//        if (!isOk) {
//            return ResultData.fail("修改失败！请重试！");
//        }
//        return ResultData.success("修改成功！");
//    }


    /**
     * @des： 删除用户，只有超级管理员有这个权限
     *
     * @param params
     * @return
     */
    @DeleteMapping("/delete-user")
    @RequiresAuthentication
    @RequiresRoles(value = {"root"})
    public ResultData<Void> deleteUser(@RequestBody Map<String, Object> params) {
        boolean isOk = userInfoService.deleteUser((List<String>) params.get("ids"));
        if (!isOk) {
            return ResultData.fail("删除失败！请重试！");
        }
        return ResultData.success("删除成功！");
    }
}

