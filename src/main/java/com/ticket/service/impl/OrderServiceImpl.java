package com.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.entity.Order;
import com.ticket.entity.TicketType;
import com.ticket.mapper.OrderMapper;
import com.ticket.mapper.TicketTypeMapper;
import com.ticket.service.OrderService;
import com.ticket.vo.OrderDetailVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TicketTypeMapper ticketTypeMapper;

    @Override
    public Page<Order> getOrderList(Long userId, Integer page, Integer size) {
        Page<Order> orderPage = new Page<>(page, size);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        // TODO：返回的字段需要挑选,创建dto
        return orderMapper.selectPage(orderPage, queryWrapper);
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectOne(new QueryWrapper<Order>()
                .eq("id", orderId).eq("user_id", userId));

        if (order == null) {
            // TODO：报错
            throw new RuntimeException("订单" + orderId + "不存在");
        }

        OrderDetailVO detailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order, detailVO);

        // 获取票档信息
        // TODO：没看懂TicketType和Session的区别
        TicketType ticketType = ticketTypeMapper.selectById(order.getTicketTypeId());
        detailVO.setTicketType(ticketType);

        return detailVO;
    }

    @Override
    // TODO：记得开启事务，增删改
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectOne(new QueryWrapper<Order>()
                .eq("id", orderId).eq("user_id", userId));

        if (order != null && order.getStatus() == 0) { // 待支付状态才能取消
            order.setStatus(2); // 已取消
            orderMapper.updateById(order);
        }

        // TODO：取消订单，需要把库存数量恢复
    }
}