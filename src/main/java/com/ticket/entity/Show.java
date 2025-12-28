package com.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("show")
public class Show {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String coverImage;
    private String city;
    private String category; // 演唱会、话剧、音乐会、舞蹈
    private Integer status; // 0:未开票 1:已开票 2:已结束

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}