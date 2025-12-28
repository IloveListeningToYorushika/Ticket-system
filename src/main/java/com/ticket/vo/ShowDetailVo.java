package com.ticket.vo;

import com.ticket.entity.Session;
import com.ticket.entity.Show;
import com.ticket.entity.TicketType;
import lombok.Data;
import java.util.List;

@Data
public class ShowDetailVO {
    private Show show;
    private List<Session> sessions;
    private List<TicketType> ticketTypes;
}