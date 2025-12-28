package com.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ticket_type")
public class TicketType {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sessionId;
    private String name;
    private BigDecimal price;
    private Integer totalStock;
    private Integer availableStock;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}