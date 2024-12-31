package com.xiao.xoj.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author 肖恩
 * @create 2023/6/9 9:26
 * @description: TODO
 */
public class JwtToken implements AuthenticationToken {

    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
