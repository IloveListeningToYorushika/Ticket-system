package com.ticket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.common.Result;
import com.ticket.common.utils.UserContext;
import com.ticket.dto.OrderCancelDTO;
import com.ticket.dto.OrderListDTO;
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
     * 使用UserContext获取用户ID，返回精简的DTO
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
     * 使用UserContext获取用户ID
     */
    @GetMapping("/{id}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        return Result.success(orderService.getOrderDetail(userId, id));
    }

    /**
     * 取消订单
     * 使用UserContext获取用户ID
     */
    @PutMapping("/{id}/cancel")
    public Result cancelOrder(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        orderService.cancelOrder(userId, id);
        return Result.success();
    }

    /**
     * 取消订单（返回详细信息）
     * 满足会议纪要中提到的用户体验需求
     */
    @PutMapping("/{id}/cancel-detail")
    public Result<OrderCancelDTO> cancelOrderWithDetail(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        return Result.success(orderService.cancelOrderWithDetail(userId, id));
    }
}