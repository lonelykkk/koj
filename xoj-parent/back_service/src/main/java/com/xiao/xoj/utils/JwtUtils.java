package com.xiao.xoj.utils;

import com.xiao.xoj.shiro.ShiroConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author 肖恩
 * @create 2023/6/9 9:30
 * @description: TODO
 */
@Slf4j
@Data
@Component
public class JwtUtils {

    @Value("${jwt-token-secret}")
    private String secret;

    @Value("${jwt-token-expire}")
    private long expire;

    @Value("${jwt-token-fresh-expire}")
    private long checkRefreshExpire;

    @Resource
    private RedisUtils redisUtils;


    /**
     * @des: 生成token
     *
     * @param userId
     * @return
     */
    public String generateToken(String userId) {
        Date nowDate = new Date();
        // 过期时间
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);

        String token = Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setSubject(userId)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        // 放入redis中
        redisUtils.set(ShiroConstant.SHIRO_TOKEN_KEY + userId, token, expire);
        redisUtils.set(ShiroConstant.SHIRO_TOKEN_REFRESH + userId, "1", checkRefreshExpire);
        return token;
    }


    /**
     * @des： 解析token
     *
     * @param token
     * @return
     */
    public Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.debug("validate is token error ", e);
            return null;
        }
    }


    /**
     * @des： 清除token
     *
     * @param userId
     * @return
     */
    public void cleanToken(String userId) {
        redisUtils.del(ShiroConstant.SHIRO_TOKEN_KEY + userId, ShiroConstant.SHIRO_TOKEN_REFRESH + userId);
    }


    /**
     * @des： token是否存在
     *
     * @param userId
     * @return
     */
    public boolean hasToken(String userId) {
        return redisUtils.hasKey(ShiroConstant.SHIRO_TOKEN_KEY + userId);
    }


    /**
     * @des: token是否过期
     *
     * @param expiration
     * @return
     */
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }


}
