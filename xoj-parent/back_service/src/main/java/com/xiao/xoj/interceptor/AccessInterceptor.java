package com.xiao.xoj.interceptor;

import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 肖恩
 * @create 2023/7/8 18:33
 * @description: TODO
 */
@Component
public class AccessInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null)
            return true;

        // 判断是否已过期
        Claims claim = jwtUtils.getClaimByToken(token);
        if (claim == null || jwtUtils.isTokenExpired(claim.getExpiration())) {
            throw new AuthenticationException("登录状态已失效，请重新登录！");
        }
        String userId = claim.getSubject();

        // 如果校验请求携带的token 与 redis缓存对应的token
        // 那就会造成一个地方登录，另一个地方老的token就直接失效。
        // 对于OJ来说，允许多地方登录在线。
        boolean hasToken = jwtUtils.hasToken(userId);
        if (!hasToken) {
            throw new AuthenticationException("登录状态已失效，请重新登录！");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
