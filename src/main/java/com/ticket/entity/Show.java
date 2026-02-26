package com.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("show")//这个实体类对应数据库的show表
public class Show {
    @TableId(type = IdType.AUTO)//主键是ID，采用数据库自增；唯一标识的一场演出
    private Long id;

    private String name;
    private String description;
    private String coverImage;
    private String city;
    private String category; // 演唱会、话剧、音乐会、舞蹈
    private Integer status; // 0:未开票 1:已开票 2:已结束

    @TableField(fill = FieldFill.INSERT)//这个字段在插入记录时会自动填充，配合数据库的自动填充功能
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//这个字段在插入和更新的时候都会自动填充，每次更新记录时会更新为当前时间
    private LocalDateTime updateTime;
}