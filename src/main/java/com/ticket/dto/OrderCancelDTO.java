package com.ticket.dto;

import lombok.Data;

/**
 * 订单取消返回DTO，包含取消订单时需要展示的关键信息
 * 满足会议纪要中提到的用户体验需求
 */
@Data
public class OrderCancelDTO {
    private String showName;
    private String showCover;
    private String sessionTime;
    private String ticketTypeName;
    private Integer ticketCount;
    private Double refundAmount;
    private String orderNo;
}