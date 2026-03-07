package com.ticket.vo;

import com.ticket.entity.Order;
import com.ticket.entity.TicketType;
import lombok.Data;

@Data
public class OrderDetailVo extends Order {
    private TicketType ticketType;
}

//该OrderDetailVO类用于接口参数和返回结果的封装