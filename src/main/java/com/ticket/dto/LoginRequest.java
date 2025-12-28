package com.ticket.dto;

import lombok.Data;

@Data
public class LoginRequest {
    // 登录方式 1:密码登录 2:邮箱登录 3:微信登录
    private Integer loginType;

    // 密码登录字段
    private String username;
    private String password;

    // 邮箱登录字段
    private String email;

    // 微信登录字段
    private String wechatOpenid;
}