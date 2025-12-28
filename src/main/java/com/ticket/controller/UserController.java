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
    public Result getUserInfo(@RequestAttribute("userId") Long userId) {
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