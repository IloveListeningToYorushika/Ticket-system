package com.ticket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ticket.common.Result;
import com.ticket.dto.LoginRequest;
import com.ticket.dto.UserInfoDTO;
import com.ticket.entity.User;

public interface UserService extends IService<User> {

    Result login(LoginRequest request);

    Result logout(String token);

    User getUserById(Long userId);

    UserInfoDTO getUserInfoDTO(Long userId);

    void updateUserInfo(Long userId, UserInfoDTO userInfoDTO);
}