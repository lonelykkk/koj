package com.xiao.xoj.shiro;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiao.xoj.mapper.RolePermissionMapper;
import com.xiao.xoj.pojo.entity.user.Permission;
import com.xiao.xoj.pojo.entity.user.Role;
import com.xiao.xoj.pojo.entity.user.UserInfo;
import com.xiao.xoj.service.UserInfoService;
import com.xiao.xoj.service.UserRoleService;
import com.xiao.xoj.utils.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/6/8 21:31
 * @description: 自定义Realm
 */
@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserRoleService userRoleService;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    /**
     重新AuthenticatingRealm中的supports
     public boolean supports(AuthenticationToken token) {
        return token != null && this.getAuthenticationTokenClass().isAssignableFrom(token.getClass());
     }

     */
    // 确保AccountRealm只对JwtToken生效
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    // 授权处理
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("授权处理");
        AccountProfile accountProfile = (AccountProfile)principalCollection.getPrimaryPrincipal();
        // 角色权限列表
        List<String> permissionNameList = new LinkedList<>();
        // 用户角色列表
        List<String> roleNameList = new LinkedList<>();

        // 获取该用户的所有权限
        List<Role> roles = userRoleService.getRolesByUid(accountProfile.getId());
        for (Role role : roles) {
            roleNameList.add(role.getName());
            List<Permission> permissions = rolePermissionMapper.getAuthsByRid(role.getId());
            if (permissions != null && permissions.size() > 0) {
                for (Permission permission : permissions) {
                    permissionNameList.add(permission.getAuth());
                }
            }
        }

        // 添加权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRoles(roleNameList);
        simpleAuthorizationInfo.addStringPermissions(permissionNameList);

        return simpleAuthorizationInfo;
    }


    // 认证处理
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("认证处理");
        JwtToken token = (JwtToken) authenticationToken;

        String userId = jwtUtils.getClaimByToken((String) token.getPrincipal()).getSubject();

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        UserInfo userInfo = userInfoService.getOne(queryWrapper);

        if (userInfo == null) {
            throw new UnknownAccountException("账户不存在！");
        }
        if (userInfo.getIsDisabled()) {
            throw new LockedAccountException("该账户已被封禁!");
        }

        AccountProfile accountProfile = new AccountProfile();
        accountProfile.setUid(userInfo.getId());
        accountProfile.setUsername(userInfo.getUsername());
        accountProfile.setNickname(userInfo.getNickname());
        accountProfile.setRealname(userInfo.getRealname());
        accountProfile.setClasse(userInfo.getClasse());
        accountProfile.setNumber(userInfo.getNumber());
        accountProfile.setAvatar(userInfo.getAvatar());
        accountProfile.setIsDisabled(userInfo.getIsDisabled());

        return new SimpleAuthenticationInfo(accountProfile, token.getCredentials(), this.getName());
    }
}
