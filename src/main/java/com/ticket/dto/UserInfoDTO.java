package com.ticket.dto;

import lombok.Data;

/**
 * 用户信息DTO，用于返回给前端的安全数据
 * 避免直接暴露User实体类中的敏感信息（如密码）
 */
@Data
public class UserInfoDTO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private Integer status;
    private String avatar;
    private String wechatOpenid;
}