package com.ticket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ticket.entity.Order;
import com.ticket.vo.OrderDetailVO;

public interface OrderService extends IService<Order> {

    Page<Order> getOrderList(Long userId, Integer page, Integer size);

    OrderDetailVO getOrderDetail(Long userId, Long orderId);

    void cancelOrder(Long userId, Long orderId);
}