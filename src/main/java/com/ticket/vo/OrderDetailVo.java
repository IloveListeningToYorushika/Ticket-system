package com.ticket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单详情 VO
 */
@Data
public class OrderDetailVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long showId;
    private Long sessionId;
    private Long ticketTypeId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Integer status;
    private LocalDateTime payTime;
    private String payOrderNo;
    private LocalDateTime createTime;

    // 补充的信息
    private String showName;
    private String showCover;
    private LocalDateTime sessionTime;
    private String ticketTypeName;
    private BigDecimal price;
}