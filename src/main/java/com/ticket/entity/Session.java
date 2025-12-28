package com.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("session")
public class Session {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long showId;
    private String venue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalStock;
    private Integer availableStock;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}