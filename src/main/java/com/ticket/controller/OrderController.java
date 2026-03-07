package com.ticket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.common.Result;
import com.ticket.common.context.UserContext;
import com.ticket.dto.OrderCancelDTO;
import com.ticket.dto.OrderCreateDTO;
import com.ticket.dto.OrderListDTO;
import com.ticket.entity.Order;
import com.ticket.service.OrderService;
import com.ticket.vo.OrderDetailVO;
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
    public Result<Page<OrderListDTO>> getOrderList(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        return Result.success(orderService.getOrderListDTO(userId, page, size));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        try {
            return Result.success(orderService.getOrderDetail(userId, id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result cancelOrder(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        try {
            orderService.cancelOrder(userId, id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消订单（返回详细信息）
     */
    @PutMapping("/{id}/cancel-detail")
    public Result<OrderCancelDTO> cancelOrderWithDetail(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        try {
            return Result.success(orderService.cancelOrderWithDetail(userId, id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<Order> createOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        try {
            return Result.success(orderService.createOrder(userId, orderCreateDTO));
        } catch (IllegalArgumentException | RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay")
    public Result payOrder(@RequestParam String orderNo, @RequestParam String payOrderNo) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        try {
            orderService.payOrder(userId, orderNo, payOrderNo);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}