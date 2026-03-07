package com.ticket.dto;

import lombok.Data;

/**
 * 订单创建请求 DTO
 */
@Data
public class OrderCreateDTO {
    private Long showId;
    private Long sessionId;
    private Long ticketTypeId;
    private Integer quantity;
}