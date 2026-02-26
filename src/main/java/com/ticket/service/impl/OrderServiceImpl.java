package com.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.dto.OrderCancelDTO;
import com.ticket.dto.OrderListDTO;
import com.ticket.entity.Order;
import com.ticket.entity.Session;
import com.ticket.entity.Show;
import com.ticket.entity.TicketType;
import com.ticket.mapper.OrderMapper;
import com.ticket.mapper.SessionMapper;
import com.ticket.mapper.ShowMapper;
import com.ticket.mapper.TicketTypeMapper;
import com.ticket.service.OrderService;
import com.ticket.vo.OrderDetailVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TicketTypeMapper ticketTypeMapper;

    @Autowired
    private SessionMapper sessionMapper;

    @Autowired
    private ShowMapper showMapper;

    @Override
    public Page<Order> getOrderList(Long userId, Integer page, Integer size) {
        Page<Order> orderPage = new Page<>(page, size);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        return orderMapper.selectPage(orderPage, queryWrapper);
    }

    @Override
    public Page<OrderListDTO> getOrderListDTO(Long userId, Integer page, Integer size) {
        Page<Order> orderPage = new Page<>(page, size);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");

        Page<Order> orderResult = orderMapper.selectPage(orderPage, queryWrapper);

        // 转换为DTO，只返回前端需要的字段
        Page<OrderListDTO> dtoPage = new Page<>(orderResult.getCurrent(), orderResult.getSize(), orderResult.getTotal());
        List<OrderListDTO> dtoList = orderResult.getRecords().stream().map(order -> {
            OrderListDTO dto = new OrderListDTO();
            BeanUtils.copyProperties(order, dto);

            // 补充演出和场次信息
            Session session = sessionMapper.selectById(order.getSessionId());
            if (session != null) {
                dto.setSessionTime(session.getStartTime().toString());
                Show show = showMapper.selectById(session.getShowId());
                if (show != null) {
                    dto.setShowName(show.getName());
                    dto.setShowCover(show.getCoverImage());
                    dto.setShowId(show.getId());
                }
            }

            // 补充票种信息
            TicketType ticketType = ticketTypeMapper.selectById(order.getTicketTypeId());
            if (ticketType != null) {
                dto.setTicketTypeName(ticketType.getName());
            }

            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看此订单");
        }

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order, orderDetailVO);

        // 补充演出信息
        Session session = sessionMapper.selectById(order.getSessionId());
        if (session != null) {
            orderDetailVO.setSessionTime(session.getStartTime());
            Show show = showMapper.selectById(session.getShowId());
            if (show != null) {
                orderDetailVO.setShowName(show.getName());
                orderDetailVO.setShowCover(show.getCoverImage());
            }
        }

        // 补充票种信息
        TicketType ticketType = ticketTypeMapper.selectById(order.getTicketTypeId());
        if (ticketType != null) {
            orderDetailVO.setTicketTypeName(ticketType.getName());
            orderDetailVO.setPrice(ticketType.getPrice());
        }

        return orderDetailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if (order.getStatus() != 0) { // 假设0表示待支付状态
            throw new RuntimeException("订单状态不允许取消");
        }

        // 更新订单状态为已取消
        order.setStatus(2); // 假设2表示已取消
        orderMapper.updateById(order);

        // 恢复库存
        TicketType ticketType = ticketTypeMapper.selectById(order.getTicketTypeId());
        if (ticketType != null) {
            ticketType.setAvailableStock(ticketType.getAvailableStock() + order.getQuantity());
            ticketTypeMapper.updateById(ticketType);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCancelDTO cancelOrderWithDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if (order.getStatus() != 0) { // 假设0表示待支付状态
            throw new RuntimeException("订单状态不允许取消");
        }

        // 获取订单详细信息用于返回
        OrderCancelDTO cancelDTO = new OrderCancelDTO();
        cancelDTO.setOrderNo(order.getOrderNo());
        cancelDTO.setTicketCount(order.getQuantity());
        cancelDTO.setRefundAmount(order.getTotalPrice().doubleValue());

        // 补充演出和场次信息
        Session session = sessionMapper.selectById(order.getSessionId());
        if (session != null) {
            cancelDTO.setSessionTime(session.getStartTime().toString());
            Show show = showMapper.selectById(session.getShowId());
            if (show != null) {
                cancelDTO.setShowName(show.getName());
                cancelDTO.setShowCover(show.getCoverImage());
            }
        }

        // 补充票种信息
        TicketType ticketType = ticketTypeMapper.selectById(order.getTicketTypeId());
        if (ticketType != null) {
            cancelDTO.setTicketTypeName(ticketType.getName());
        }

        // 更新订单状态为已取消
        order.setStatus(2); // 假设2表示已取消
        orderMapper.updateById(order);

        // 恢复库存
        if (ticketType != null) {
            ticketType.setAvailableStock(ticketType.getAvailableStock() + order.getQuantity());
            ticketTypeMapper.updateById(ticketType);
        }

        return cancelDTO;
    }
}