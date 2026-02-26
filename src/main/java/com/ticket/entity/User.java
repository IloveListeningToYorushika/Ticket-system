
package com.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")//对应数据库里的user表
public class User {
    @TableId(type = IdType.AUTO)//实体自己的主键ID（用户ID），唯一标识的一个用户
    private Long id;

    private String username;
    private String password;
    private String email;
    private String phone;
    private String wechatOpenid;
    private Integer status; // 0:禁用 1:启用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}