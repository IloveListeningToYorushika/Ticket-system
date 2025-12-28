package com.ticket.vo;

import com.ticket.entity.Session;
import com.ticket.entity.Show;
import lombok.Data;
import java.util.List;

@Data
public class ShowListVO extends Show {
    private List<Session> sessions;
}