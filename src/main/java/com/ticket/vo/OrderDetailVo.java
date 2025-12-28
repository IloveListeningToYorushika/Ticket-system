package com.ticket.vo;

import com.ticket.entity.Order;
import com.ticket.entity.TicketType;
import lombok.Data;

@Data
public class OrderDetailVO extends Order {
    private TicketType ticketType;
}