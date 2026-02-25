package com.ticket.common.interceptor;

import com.ticket.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            // TODO：报错信息要写清楚
            return false;
        }

        token = token.replace("Bearer ", "");

        // 验证token是否在redis中存在
        String userId = (String) redisTemplate.opsForValue().get("TOKEN_" + token);
        if (userId == null) {
            response.setStatus(401);
            return false;
        }

        try {
            Claims claims = jwtUtil.getClaimsByToken(token);
            // TODO：没有讨论解析失败的情况，可能是因为token过期了
            request.setAttribute("userId", Long.valueOf(claims.getSubject()));
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
}