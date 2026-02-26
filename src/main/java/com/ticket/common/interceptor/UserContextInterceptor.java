package com.ticket.common.interceptor;

import com.ticket.common.utils.JwtUtil;
import com.ticket.common.utils.UserContext;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户上下文拦截器，用于在线程中设置用户信息
 * 替代原来在每个Controller方法中传递userId参数的方式
 */
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "");
            
            // 验证token是否在redis中存在
            String userId = (String) redisTemplate.opsForValue().get("TOKEN_" + token);
            if (userId != null) {
                try {
                    Claims claims = jwtUtil.getClaimsByToken(token);
                    UserContext.setUserId(Long.valueOf(claims.getSubject()));
                } catch (Exception e) {
                    // 解析失败时不设置用户上下文，让JwtInterceptor处理认证失败
                }
            }
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清除线程中的用户信息，防止内存泄漏
        UserContext.clear();
    }
}