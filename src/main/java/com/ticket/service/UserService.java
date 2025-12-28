package com.ticket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ticket.common.Result;
import com.ticket.dto.LoginRequest;
import com.ticket.entity.User;

public interface UserService extends IService<User> {

    Result login(LoginRequest request);

    Result logout(String token);

    User getUserById(Long userId);

    void updateUserInfo(User user);
}