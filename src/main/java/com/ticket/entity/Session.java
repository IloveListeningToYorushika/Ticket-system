package com.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("session")//这个实体类对应数据库的session表，唯一标识的的一个场次
public class Session {
    @TableId(type = IdType.AUTO)//主键是ID，可以采用自增
    private Long id;//定义主键字段，类型为long

    private Long showId;
    private String venue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalStock;
    private Integer availableStock;

    @TableField(fill = FieldFill.INSERT)//这个字段在插入记录时会自动填充，配合数据库的自动填充功能
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//这个字段在插入和更新的时候都会自动填充，每次更新记录时会更新为当前时间
    private LocalDateTime updateTime;
}