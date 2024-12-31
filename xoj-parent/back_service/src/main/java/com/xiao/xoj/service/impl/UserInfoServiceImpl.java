package com.xiao.xoj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.mapper.UserRoleMapper;
import com.xiao.xoj.pojo.dto.AdminEditUserDTO;
import com.xiao.xoj.pojo.dto.LoginDTO;
import com.xiao.xoj.pojo.entity.user.Role;
import com.xiao.xoj.pojo.entity.user.UserInfo;
import com.xiao.xoj.mapper.UserInfoMapper;
import com.xiao.xoj.pojo.entity.user.UserRole;
import com.xiao.xoj.pojo.vo.UserInfoVO;
import com.xiao.xoj.pojo.vo.UserRolesVO;
import com.xiao.xoj.service.RoleService;
import com.xiao.xoj.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.service.UserRoleService;
import com.xiao.xoj.shiro.AccountProfile;
import com.xiao.xoj.utils.Constants;
import com.xiao.xoj.utils.JwtUtils;
import com.xiao.xoj.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfoVO login(LoginDTO loginDTO) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        String key = Constants.Account.TRY_LOGIN_NUM.getCode() + loginDTO.getUsername();
        Integer tryLoginNum = (Integer) redisUtils.get(key);
        if (tryLoginNum != null && tryLoginNum >= 10) {
            throw new StatusFailException("对不起！登录失败次数过多！您的账号有风险，一小时内暂时无法登录！");
        }

        UserRolesVO userRolesVO = userRoleService.getUserRoles(null, loginDTO.getUsername());
        if (userRolesVO == null) {
            throw new StatusFailException("用户名错误！");
        }

        if(!userRolesVO.getPassword().equals(SecureUtil.md5(loginDTO.getPassword()))) {
            // 如果登录失败，统计登录次数
            if (tryLoginNum == null) {
                redisUtils.set(key, 1, 60 * 60);
            } else {
                redisUtils.set(key, tryLoginNum + 1, 60 * 60);
            }
            throw new StatusFailException("密码错误！");
        }

        if (userRolesVO.getIsDisabled()) {
            throw new StatusFailException("该账号已被封禁！请联系管理员！");
        }

        // 登录成功，清除限制
        if (tryLoginNum != null) {
            redisUtils.del(key);
        }

        // 查询用户角色
        List<String> roleList = new LinkedList<>();
        userRolesVO.getRoles().stream().forEach(role -> roleList.add(role.getName()));

        if (roleList.contains("admin") || roleList.contains("root")) {
            String token = jwtUtils.generateToken(userRolesVO.getId());

            response.setHeader("Authorization", token); //放到信息头部
            response.setHeader("Access-Control-Expose-Headers", "Authorization");

            UserInfoVO userInfoVO = new UserInfoVO();
            BeanUtil.copyProperties(userRolesVO, userInfoVO, "roles");
            userInfoVO.setRoleList(userRolesVO.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
            return userInfoVO;
        } else {
            throw new StatusFailException("该账号并非管理员账号！无权登录！");
        }
    }


    @Override
    public void logout() {
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        jwtUtils.cleanToken(accountProfile.getId());
        SecurityUtils.getSubject().logout();
    }


    @Override
    public IPage<UserRolesVO> getUserList(Integer limit, Integer currentPage, Boolean onlyAdmin, String keyword) {
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        if (keyword != null) {
            keyword = keyword.trim();
        }
        Page<UserRolesVO> page = new Page<>(currentPage, limit);
        if (onlyAdmin) {
            return userRoleMapper.getAdminUserList(page, keyword);
        } else {
            return userRoleMapper.getUserList(page, keyword);
        }
    }


    @Override
    public boolean editUser(AdminEditUserDTO adminEditUserDTO) {

        String username = adminEditUserDTO.getUsername();
        String uid = adminEditUserDTO.getId();
        String realname = adminEditUserDTO.getRealname();
        String email = adminEditUserDTO.getEmail();
        String password = adminEditUserDTO.getPassword();
        int type = adminEditUserDTO.getType();
        String number = adminEditUserDTO.getNumber();
        String classe = adminEditUserDTO.getClasse();
        int sex = adminEditUserDTO.getSex();
        boolean isDisabled = adminEditUserDTO.getIsDisabled();
        boolean setNewPwd = adminEditUserDTO.getSetNewPwd();

        // 参数校验
        if (!StringUtils.isEmpty(realname) && realname.length() > 50) {
            throw new StatusFailException("真实姓名的长度不能超过50位");
        }

        if (!StringUtils.isEmpty(password) && (password.length() < 6 || password.length() > 20)) {
            throw new StatusFailException("密码长度建议为6~20位！");
        }

        if (username.length() > 20) {
            throw new StatusFailException("用户名长度建议不能超过20位!");
        }

        if (!StringUtils.isEmpty(number) && (number.length() != 10)) {
            throw new StatusFailException("学号长度为10位！请重新填写！");
        }

        if (!StringUtils.isEmpty(classe) && (classe.length() != 6)) {
            throw new StatusFailException("班级格式长度为8位，如：软件2001！请重新填写！");
        }

        UpdateWrapper<UserInfo> userInfoUpdateWrapper = new UpdateWrapper<>();

        userInfoUpdateWrapper.eq("id", uid)
                .set("username", username)
                .set("realname", realname)
                .set("email", email)
                .set(setNewPwd, "password", SecureUtil.md5(password))
                .set("number", number)
                .set("classe", classe)
                .set("sex", sex)
                .set("is_disabled", isDisabled);

        UserInfo userInfo = this.getById(uid);
        boolean updateUserInfo = this.update(userInfo, userInfoUpdateWrapper);

        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.eq("user_id", uid);
        UserRole userRole = userRoleService.getOne(userRoleQueryWrapper, false);
        boolean changeUserRole = false;
        int oldType = userRole.getRoleId().intValue();
        if (userRole.getRoleId().intValue() != type) {
            userRole.setRoleId(type);
            changeUserRole = userRoleService.updateById(userRole);
            if (type == 1 || oldType == 2) {
                // 新增或者去除超级管理员需要删除缓存
                String cacheKey = Constants.Account.SUPER_ADMIN_UID_LIST_CACHE.getCode();
                redisUtils.del(cacheKey);
            }
        }
        if (updateUserInfo && setNewPwd) {
            // 需要重新登录
            userRoleService.deleteCache(uid, true);
        } else if (changeUserRole) {
            // 需要重新授权
            userRoleService.deleteCache(uid, false);
        }

        // todo 消息通知
//        if (changeUserRole) {
//            // 获取当前登录的用户
//            AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//            String title = "权限变更通知(Authority Change Notice)";
//            String content = userRoleEntityService.getAuthChangeContent(oldType, type);
//            adminNoticeManager.addSingleNoticeToUser(userRolesVo.getUid(), uid, title, content, "Sys");
//        }

        return true;
    }


    @Override
    public boolean deleteUser(List<String> deleteUserIdList) {
        boolean isOk = this.removeByIds(deleteUserIdList);
        if (!isOk) {
            throw new StatusFailException("删除失败！");
        }
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        log.info("[{}],[{}],uidList:[{}],operatorUid:[{}],operatorUsername:[{}]",
                "Admin_User", "Delete", deleteUserIdList, userRolesVo.getUid(), userRolesVo.getUsername());
        return true;
    }


}
