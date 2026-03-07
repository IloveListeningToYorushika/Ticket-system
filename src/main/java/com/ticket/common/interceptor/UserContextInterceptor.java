package com.ticket.common.interceptor;

import com.ticket.common.utils.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户上下文拦截器，用于在线程中设置用户信息
 * 依赖 JwtInterceptor 先完成认证，从 request 中获取 userId
 */
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 request 属性中获取 JwtInterceptor 设置的 userId
        Long userId = (Long) request.getAttribute("userId");
        if (userId != null) {
            // 设置到线程上下文中
            UserContext.setUserId(userId);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清除线程中的用户信息，防止内存泄漏
        UserContext.clear();
    }
}