package com.xiao.xoj.service.impl;

import com.xiao.xoj.pojo.entity.user.Role;
import com.xiao.xoj.pojo.entity.user.UserRole;
import com.xiao.xoj.mapper.UserRoleMapper;
import com.xiao.xoj.pojo.vo.UserRolesVO;
import com.xiao.xoj.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.xoj.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 肖恩
 * @since 2023-03-19
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RedisSessionDAO redisSessionDAO;

    @Override
    public List<Role> getRolesByUid(String userId) {
        return userRoleMapper.getRolesByUid(userId);
    }

    @Override
    public UserRolesVO getUserRoles(String userId, String username) {
        return userRoleMapper.getUserRoles(userId, username);
    }

    /**
     * @param uid             当前需要操作的用户id
     * @param isRemoveSession 如果为true则会强行删除该用户session，必须重新登陆，false的话 在访问受限接口时会重新授权
     * @MethodName deleteCache
     * @Description TODO
     * @Return
     * @Since 2021/6/12
     */
    @Override
    public void deleteCache(String uid, boolean isRemoveSession) {
        //从缓存中获取Session
        Collection<Session> sessions = redisSessionDAO.getActiveSessions();
        for (Session sessionInfo : sessions) {
            //遍历Session,找到该用户名称对应的Session
            Object attribute = sessionInfo.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (attribute == null) {
                continue;
            }
            AccountProfile accountProfile = (AccountProfile) ((SimplePrincipalCollection) attribute).getPrimaryPrincipal();
            if (accountProfile == null) {
                continue;
            }
            // 如果该session是指定的uid用户的
            if (Objects.equals(accountProfile.getUid(), uid)) {
                deleteSession(isRemoveSession, sessionInfo, attribute);
            }
        }

    }


    private void deleteSession(boolean isRemoveSession, Session session, Object attribute) {
        //删除session 会强制退出！主要是在禁用用户或角色时，强制用户退出的
        if (isRemoveSession) {
            redisSessionDAO.delete(session);
        }

        //删除Cache，在访问受限接口时会重新授权
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        Authenticator authc = securityManager.getAuthenticator();
        ((LogoutAware) authc).onLogout((SimplePrincipalCollection) attribute);
    }
}
