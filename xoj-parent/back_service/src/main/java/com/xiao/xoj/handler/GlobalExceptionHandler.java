package com.xiao.xoj.handler;

import com.xiao.xoj.common.exception.StatusFailException;
import com.xiao.xoj.common.exception.StatusForbiddenException;
import com.xiao.xoj.common.exception.StatusNotFoundException;
import com.xiao.xoj.common.exception.StatusSystemErrorException;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.common.result.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 肖恩
 * @create 2023/3/21 15:09
 * @description: TODO
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * @des: 40000  自定义通用异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler({
            StatusFailException.class,
            StatusNotFoundException.class,
            StatusForbiddenException.class,
            StatusSystemErrorException.class
    })
    public ResultData<Void> handlerCustomException(Exception e) {
        return ResultData.fail(ResultStatus.FAIL.getStatus(), e.getMessage());
    }


    /**
     * 401 -UnAuthorized 处理AuthenticationException,token相关异常 即是认证出错 可能无法处理！
     *
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = AuthenticationException.class)
    public ResultData<Void> handleAuthenticationException(AuthenticationException e,
                                                            HttpServletRequest httpRequest,
                                                            HttpServletResponse httpResponse) {
        System.out.println("global ");
        httpResponse.setHeader("Url-Type", httpRequest.getHeader("Url-Type")); // 为了前端能区别请求来源
        return ResultData.fail(ResultStatus.ACCESS_DENIED.getStatus(), "请您先登录！");
    }

    /**
     * 401 -UnAuthorized UnauthenticatedException,token相关异常 即是认证出错 可能无法处理！
     * 没有登录（没有token），访问有@RequiresAuthentication的请求路径会报这个异常
     */
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ExceptionHandler(value = UnauthenticatedException.class)
//    public ResultData<Void> handleUnauthenticatedException(UnauthenticatedException e,
//                                                             HttpServletRequest httpRequest,
//                                                             HttpServletResponse httpResponse) {
//        httpResponse.setHeader("Url-Type", httpRequest.getHeader("Url-Type")); // 为了前端能区别请求来源
//        return ResultData.fail(ResultStatus.ACCESS_DENIED.getStatus(), "请您先登录！");
//    }


    /**
     * 403 -FORBIDDEN AuthorizationException异常 即是授权认证出错 可能无法处理！
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = AuthorizationException.class)
    public ResultData<Void> handleAuthenticationException(AuthorizationException e,
                                                          HttpServletRequest httpRequest,
                                                          HttpServletResponse httpResponse) {
        httpResponse.setHeader("Url-Type", httpRequest.getHeader("Url-Type")); // 为了前端能区别请求来源
        return ResultData.fail(ResultStatus.FORBIDDEN.getStatus(), "对不起，您无权限进行此操作！");
    }

    /**
     * 403 -FORBIDDEN 处理shiro的异常 无法处理！ 未能走到controller层
     */
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    @ExceptionHandler(value = ShiroException.class)
//    public ResultData<Void> handleShiroException(ShiroException e,
//                                                   HttpServletRequest httpRequest,
//                                                   HttpServletResponse httpResponse) {
//        httpResponse.setHeader("Url-Type", httpRequest.getHeader("Url-Type")); // 为了前端能区别请求来源
//        return ResultData.fail(ResultStatus.FORBIDDEN.getStatus(), "对不起，您无权限进行此操作，请先登录进行授权认证");
//    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResultData<Void> handler(IllegalArgumentException e) {
        return ResultData.fail(ResultStatus.FAIL.getStatus(), e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResultData<Void> error(Exception e){
        e.printStackTrace();
        return ResultData.fail();
    }

}
