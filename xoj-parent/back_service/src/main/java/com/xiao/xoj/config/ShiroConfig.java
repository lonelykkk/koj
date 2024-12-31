package com.xiao.xoj.config;

import com.xiao.xoj.shiro.AccountRealm;
import com.xiao.xoj.shiro.JwtFilter;
import com.xiao.xoj.shiro.ShiroCacheManager;
import com.xiao.xoj.shiro.ShiroConstant;
import com.xiao.xoj.utils.RedisUtils;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 肖恩
 * @create 2023/6/8 21:33
 * @description: shiro启用注解拦截控制器
 */
@Configuration
public class ShiroConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${jwt-token-expire}")
    private long expireTime;


    // 创建安全管理器
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(AccountRealm accountRealm) {

        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager(accountRealm);

        // 设置资自定义的CacheManage
        ShiroCacheManager shiroCacheManager = new ShiroCacheManager();
        shiroCacheManager.setCacheLive(expireTime);
        shiroCacheManager.setCacheKeyPrefix(ShiroConstant.SHIRO_AUTHORIZATION_CACHE);
        shiroCacheManager.setRedisUtils(redisUtils);
        defaultWebSecurityManager.setCacheManager(shiroCacheManager);

        // 关闭shiro自带的session，因为使用的无状态的token进行认证和授权，所以不需要session，节省服务器资源
        DefaultSubjectDAO defaultSubjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        defaultSubjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        defaultWebSecurityManager.setSubjectDAO(defaultSubjectDAO);

        return defaultWebSecurityManager;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/**", "jwt"); // 所有的请求需要通过自定义的Filter，即jwt
        chainDefinition.addPathDefinitions(filterMap);
        return chainDefinition;
    }

    // 创建ShiroFilter，负责拦截所有请求
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager,
                                                         ShiroFilterChainDefinition shiroFilterChainDefinition) {
        ShiroFilterFactoryBean filterFactory = new ShiroFilterFactoryBean();

        // 设置安全管理器
        filterFactory.setSecurityManager(defaultWebSecurityManager);

        // 设置自定义的filter
        Map<String, Filter> filters = new HashMap<>();
        filters.put("jwt", jwtFilter);
        filterFactory.setFilters(filters);
        Map<String, String> filterMap = shiroFilterChainDefinition.getFilterChainMap();
        filterFactory.setFilterChainDefinitionMap(filterMap);
        return filterFactory;
    }


    //开启注解代理
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    @Bean
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

}
