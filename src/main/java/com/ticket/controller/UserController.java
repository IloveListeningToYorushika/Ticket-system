package com.ticket.controller;

import com.ticket.common.Result;
import com.ticket.common.utils.UserContext;
import com.ticket.dto.UserInfoDTO;
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
     * 使用UserContext获取用户ID，避免参数传递
     */
    @GetMapping("/info")
    public Result<UserInfoDTO> getUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        return Result.success(userService.getUserInfoDTO(userId));
    }

    /**
     * 修改个人信息
     * 使用UserContext获取用户ID，避免参数传递
     */
    @PutMapping("/info")
    public Result updateUserInfo(@RequestBody UserInfoDTO userInfoDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        userService.updateUserInfo(userId, userInfoDTO);
        return Result.success();
    }
}