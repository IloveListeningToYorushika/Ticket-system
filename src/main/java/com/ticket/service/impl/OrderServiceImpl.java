package com.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.dto.OrderCancelDTO;
import com.ticket.dto.OrderCreateDTO;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

        // 判空处理
        if (orderResult == null || orderResult.getRecords() == null || orderResult.getRecords().isEmpty()) {
            Page<OrderListDTO> emptyPage = new Page<>(page, size, 0);
            return emptyPage;
        }

        // 转换为 DTO，只返回前端需要的字段
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
        // 参数校验
        if (orderId == null) {
            throw new IllegalArgumentException("订单 ID 不能为空");
        }

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
        // 参数校验
        if (orderId == null) {
            throw new IllegalArgumentException("订单 ID 不能为空");
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if (order.getStatus() != 0) { // 只有待支付状态才能取消
            throw new RuntimeException("订单状态不允许取消");
        }

        // 更新订单状态为已取消
        order.setStatus(2); // 2 表示已取消
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
        // 参数校验
        if (orderId == null) {
            throw new IllegalArgumentException("订单 ID 不能为空");
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if (order.getStatus() != 0) { // 只有待支付状态才能取消
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
        order.setStatus(2); // 2 表示已取消
        orderMapper.updateById(order);

        // 恢复库存
        if (ticketType != null) {
            ticketType.setAvailableStock(ticketType.getAvailableStock() + order.getQuantity());
            ticketTypeMapper.updateById(ticketType);
        }

        return cancelDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, OrderCreateDTO orderCreateDTO) {
        // 参数校验
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        if (orderCreateDTO == null) {
            throw new IllegalArgumentException("订单信息不能为空");
        }
        if (orderCreateDTO.getShowId() == null || orderCreateDTO.getSessionId() == null
                || orderCreateDTO.getTicketTypeId() == null) {
            throw new IllegalArgumentException("演出、场次、票种信息不能为空");
        }
        if (orderCreateDTO.getQuantity() == null || orderCreateDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("购票数量必须大于 0");
        }

        // 验证演出是否存在
        Show show = showMapper.selectById(orderCreateDTO.getShowId());
        if (show == null) {
            throw new RuntimeException("演出不存在");
        }

        // 验证场次是否存在且属于该演出
        Session session = sessionMapper.selectById(orderCreateDTO.getSessionId());
        if (session == null || !session.getShowId().equals(orderCreateDTO.getShowId())) {
            throw new RuntimeException("场次不存在或不属于该演出");
        }

        // 验证票种是否存在且属于该场次
        TicketType ticketType = ticketTypeMapper.selectById(orderCreateDTO.getTicketTypeId());
        if (ticketType == null || !ticketType.getSessionId().equals(orderCreateDTO.getSessionId())) {
            throw new RuntimeException("票种不存在或不属于该场次");
        }

        // 检查库存是否充足
        if (ticketType.getAvailableStock() < orderCreateDTO.getQuantity()) {
            throw new RuntimeException("库存不足");
        }

        // 计算总价
        BigDecimal totalPrice = ticketType.getPrice().multiply(new BigDecimal(orderCreateDTO.getQuantity()));

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setShowId(orderCreateDTO.getShowId());
        order.setSessionId(orderCreateDTO.getSessionId());
        order.setTicketTypeId(orderCreateDTO.getTicketTypeId());
        order.setQuantity(orderCreateDTO.getQuantity());
        order.setTotalPrice(totalPrice);
        order.setStatus(0); // 0 表示待支付

        orderMapper.insert(order);

        // 扣减库存
        ticketType.setAvailableStock(ticketType.getAvailableStock() - orderCreateDTO.getQuantity());
        ticketTypeMapper.updateById(ticketType);

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long userId, String orderNo, String payOrderNo) {
        // 参数校验
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        if (orderNo == null || orderNo.trim().isEmpty()) {
            throw new IllegalArgumentException("订单号不能为空");
        }
        if (payOrderNo == null || payOrderNo.trim().isEmpty()) {
            throw new IllegalArgumentException("支付订单号不能为空");
        }

        // 查询订单
        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_no", orderNo));
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证订单归属
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }

        // 验证订单状态
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不允许支付");
        }

        // 更新订单状态
        order.setStatus(1); // 1 表示已支付
        order.setPayOrderNo(payOrderNo);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /**
     * 生成订单号（时间戳 + 随机数）
     */
    private String generateOrderNo() {
        return System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}