package com.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;
    private Long userId;
    private Long ticketTypeId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Integer status; // 0:待支付 1:已支付 2:已取消 3:已完成
    private String payOrderNo; // 支付平台订单号

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}