package com.xiao.xoj.shiro;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xiao.xoj.annotation.AnonApi;
import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.common.result.ResultStatus;
import com.xiao.xoj.utils.JwtUtils;
import com.xiao.xoj.utils.RedisUtils;
import com.xiao.xoj.utils.ServiceContextUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 肖恩
 * @create 2023/6/9 0:08
 * @description: 自定义Filter
 */
@Component
public class JwtFilter extends AuthenticatingFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    // 判断是否进行认证，返回false会执行onAccessDenied，返回true直接访问controller
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        WebUtils.saveRequest(httpRequest);
        WebApplicationContext ctx = RequestContextUtils.findWebApplicationContext(httpRequest);
        RequestMappingHandlerMapping mapping = ctx.getBean(
                "requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        try {
            HandlerExecutionChain handler = mapping.getHandler(httpRequest);
            HandlerMethod handlerClazz = (HandlerMethod) handler.getHandler();
            // 判断请求是否访问的是公共接口，如果拥有@AnonApi注解则不再走登录认证，直接访问controller对应的方法
            AnonApi anonApi = ServiceContextUtils.getAnnotation(handlerClazz.getMethod(),
                    handlerClazz.getBeanType(),
                    AnonApi.class);
            return anonApi != null;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        // 获取 token
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader("Authorization");
        if (StrUtil.isBlank(jwt)) {
            return null;
        }

        return new JwtToken(jwt);
    }


    // 认证，判断是否可以访问
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank(token)) {
            // 没有token，说明没有登录
            return true;
        } else {
            try {
                // 判断是否已过期
                Claims claim = jwtUtils.getClaimByToken(token);
                if (claim == null || jwtUtils.isTokenExpired(claim.getExpiration())) {
                    System.out.println("token过期");
                    return true;
                }
                String userId = claim.getSubject();

                // 如果校验请求携带的token 与 redis缓存对应的token
                boolean hasToken = jwtUtils.hasToken(userId);
                if (!hasToken) {
                    System.out.println("redis中没有token");
                    return true;
                }

                // 判断是否需要更新token
                if (!redisUtils.hasKey(ShiroConstant.SHIRO_TOKEN_REFRESH + userId)) {
                    //过了需更新token时间，但是还未过期，则进行token刷新
                    HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                    HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
                    this.refreshToken(httpRequest, httpResponse, userId);
                }
            } catch (AuthenticationException e) {
                e.printStackTrace();
                return onLoginFailure(null, e, servletRequest, servletResponse);
            }
        }
        // 执行自动登录
        return executeLogin(servletRequest, servletResponse);
    }

    /**
     * 刷新Token，并更新token到前端
     *
     * @param request
     * @param userId
     * @param response
     * @return
     */
    private void refreshToken(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
        boolean lock = redisUtils.getLock(ShiroConstant.SHIRO_TOKEN_LOCK + userId, 20);// 获取锁20s
        if (lock) {
        String newToken = jwtUtils.generateToken(userId);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Authorization", newToken); //放到信息头部
        response.setHeader("Access-Control-Expose-Headers", "Refresh-Token,Authorization,Url-Type"); //让前端可用访问
        response.setHeader("Url-Type", request.getHeader("Url-Type")); // 为了前端能区别请求来源
        response.setHeader("Refresh-Token", "true"); //告知前端需要刷新token
    }
        redisUtils.releaseLock(ShiroConstant.SHIRO_TOKEN_LOCK + userId);
}


    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        try {
            //处理登录失败的异常
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            ResultData<Void> result = ResultData.fail(ResultStatus.ACCESS_DENIED.getStatus(), throwable.getMessage());
            String json = JSONUtil.toJsonStr(result);
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.setHeader("Access-Control-Expose-Headers", "Refresh-Token,Authorization,Url-Type"); //让前端可用访问
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Url-Type", httpRequest.getHeader("Url-Type")); // 为了前端能区别请求来源
            httpResponse.setStatus(ResultStatus.ACCESS_DENIED.getStatus());
            httpResponse.getWriter().print(json);
        } catch (IOException e1) {
        }
        return false;
    }


    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        httpServletResponse.setHeader("Access-Control-Expose-Headers",
                "Refresh-Token,Authorization,Url-Type,Content-disposition,Content-Type"); //让前端可用访问
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

}
