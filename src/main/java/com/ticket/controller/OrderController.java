package com.ticket.controller;

import com.ticket.common.Result;
import com.ticket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询订单列表
     */
    @GetMapping("/list")
    public Result getOrderList(@RequestAttribute("userId") Long userId,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.getOrderList(userId, page, size));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result getOrderDetail(@RequestAttribute("userId") Long userId,
                                 @PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(userId, id));
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result cancelOrder(@RequestAttribute("userId") Long userId,
                              @PathVariable Long id) {
        orderService.cancelOrder(userId, id);
        return Result.success();
    }
}