package com.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data//它会自动生成所有字段的getter,setter方法，equals(),hashcode(),toString()
@TableName("order")//指这个实体类对应数据库的哪张表，这里对应order表
public class Order {
    @TableId(type = IdType.AUTO)//表的主键，生成策略：数据库自增；唯一标识的一个订单
    private Long id;//定义主键字段，类型为long

    private String orderNo;//订单编号
    private Long userId;//下单用户的ID，关联user表
    private Long ticketTypeId;//购买的票档ID，关联ticket_type表
    private Integer quantity;
    private BigDecimal totalPrice;//订单总价，使用BigDecimal确保金额计算精确
    private Integer status; // 0:待支付 1:已支付 2:已取消 3:已完成
    private String payOrderNo; // 支付平台订单号

    @TableField(fill = FieldFill.INSERT)//这个字段在插入记录时会自动填充，配合数据库的自动填充功能
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//这个字段在插入和更新的时候都会自动填充，每次更新记录时会更新为当前时间
    private LocalDateTime updateTime;
}