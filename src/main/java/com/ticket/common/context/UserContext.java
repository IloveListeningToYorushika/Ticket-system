package com.ticket.common.context;

/**
 * 用户上下文工具类，使用ThreadLocal存储当前用户信息
 * 符合会议纪要中提到的线程上下文管理方案
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     */
    public static void setUserId(Long userId) {
        USER_HOLDER.set(userId);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        return USER_HOLDER.get();
    }

    /**
     * 清除当前线程的用户信息
     * 防止线程复用时的信息泄露
     */
    public static void clear() {
        USER_HOLDER.remove();
    }
}