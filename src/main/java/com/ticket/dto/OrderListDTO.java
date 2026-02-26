package com.ticket.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单列表DTO，只包含前端需要的字段
 * 精简返回数据，提高性能和安全性
 */
@Data
public class OrderListDTO {
    private Long id;
    private String orderNo;
    private Long showId;
    private String showName;
    private String showCover;
    private Long sessionId;
    private String sessionTime;
    private String ticketTypeName;
    private Integer ticketCount;
    private Double totalAmount;
    private Integer status;
    private LocalDateTime createTime;
}