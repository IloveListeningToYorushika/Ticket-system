package com.ticket.controller;

import com.ticket.common.Result;
import com.ticket.entity.User;
import com.ticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    // TODO:线程上下文ThreadLocal
    public Result getUserInfo(@RequestAttribute("userId") Long userId) {
        // TODO：直接返回User是否有安全问题，构建对应DTO
        return Result.success(userService.getUserById(userId));
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/info")
    public Result updateUserInfo(@RequestAttribute("userId") Long userId,
                                 @RequestBody User user) {
        user.setId(userId);
        userService.updateUserInfo(user);
        return Result.success();
    }
}