package com.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ticket_type")//对应数据库里的ticket_type表，唯一标识的一个票档
public class TicketType {
    @TableId(type = IdType.AUTO)//主键是ID
    private Long id;

    private Long sessionId;//该演出的ID
    private String name;//该演出的名字
    private BigDecimal price;
    private Integer totalStock;//该演出的总票量
    private Integer availableStock;//还可以买的票量

    @TableField(fill = FieldFill.INSERT)//这个字段在插入记录时会自动填充，配合数据库的自动填充功能
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//这个字段在插入和更新的时候都会自动填充，每次更新记录时会更新为当前时间
    private LocalDateTime updateTime;
}