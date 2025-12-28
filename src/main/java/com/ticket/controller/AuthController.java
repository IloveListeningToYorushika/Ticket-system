package com.ticket.controller;

import com.ticket.common.Result;
import com.ticket.dto.LoginRequest;
import com.ticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 登录接口（支持密码、邮箱、微信登录）
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String token) {
        return userService.logout(token);
    }
}