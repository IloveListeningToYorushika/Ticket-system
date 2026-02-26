package com.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.common.Result;
import com.ticket.common.utils.JwtUtil;
import com.ticket.dto.LoginRequest;
import com.ticket.dto.UserInfoDTO;
import com.ticket.entity.User;
import com.ticket.mapper.UserMapper;
import com.ticket.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Result login(LoginRequest request) {
        User user = null;

        // 根据不同登录方式查找用户
        if (request.getLoginType() == 1) { // 密码登录
            user = userMapper.selectOne(new QueryWrapper<User>()
                    .eq("username", request.getUsername()));
            if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return Result.error("用户名或密码错误");
            }
        } else if (request.getLoginType() == 2) { // 邮箱登录
            user = userMapper.selectOne(new QueryWrapper<User>()
                    .eq("email", request.getEmail()));
            if (user == null) {
                return Result.error("邮箱未注册");
            }
        } else if (request.getLoginType() == 3) { // 微信登录
            user = userMapper.selectOne(new QueryWrapper<User>()
                    .eq("wechat_openid", request.getWechatOpenid()));
            if (user == null) {
                return Result.error("微信未绑定");
            }
        }

        if (user.getStatus() == 0) {
            return Result.error("账户已被禁用");
        }

        // 生成token
        String token = jwtUtil.generateToken(user.getId());

        // 将token存入redis
        redisTemplate.opsForValue().set("TOKEN_" + token, user.getId().toString());

        return Result.success(token);
    }

    @Override
    public Result logout(String token) {
        // 从redis中删除token
        redisTemplate.delete("TOKEN_" + token.replace("Bearer ", ""));
        return Result.success();
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public UserInfoDTO getUserInfoDTO(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(user, userInfoDTO);
        // 不复制密码等敏感信息
        return userInfoDTO;
    }

    @Override
    public void updateUserInfo(Long userId, UserInfoDTO userInfoDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 只更新允许修改的字段
        user.setNickname(userInfoDTO.getNickname());
        user.setEmail(userInfoDTO.getEmail());
        user.setPhone(userInfoDTO.getPhone());
        user.setAvatar(userInfoDTO.getAvatar());

        userMapper.updateById(user);
    }
}