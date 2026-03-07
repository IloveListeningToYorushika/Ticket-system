package com.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.entity.Session;
import com.ticket.entity.Show;
import com.ticket.entity.TicketType;
import com.ticket.mapper.SessionMapper;
import com.ticket.mapper.ShowMapper;
import com.ticket.mapper.TicketTypeMapper;
import com.ticket.service.ShowService;
import com.ticket.vo.ShowDetailVO;
import com.ticket.vo.ShowListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowServiceImpl extends ServiceImpl<ShowMapper, Show> implements ShowService {

    @Autowired
    private ShowMapper showMapper;

    @Autowired
    private SessionMapper sessionMapper;

    @Autowired
    private TicketTypeMapper ticketTypeMapper;

    @Override
    public Page<ShowListVO> getShowList(String city, String category, Integer page, Integer size, Long userId) {
        // 参数校验
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("城市不能为空");
        }

        Page<Show> showPage = new Page<>(page, size);
        QueryWrapper<Show> queryWrapper = new QueryWrapper<>();

        // 分类筛选（可选）
        if (category != null && !category.isEmpty() && !"全部".equals(category)) {
            queryWrapper.eq("category", category);
        }

        // 查询已开票的演出（status=1）
        queryWrapper.eq("city", city).eq("status", 1);

        Page<Show> resultPage = showMapper.selectPage(showPage, queryWrapper);

        // 判空处理
        if (resultPage == null || resultPage.getRecords() == null || resultPage.getRecords().isEmpty()) {
            Page<ShowListVO> emptyPage = new Page<>(page, size, 0);
            return emptyPage;
        }

        Page<ShowListVO> voPage = new Page<>(page, size, resultPage.getTotal());
        List<ShowListVO> voList = resultPage.getRecords().stream().map(show -> {
            ShowListVO vo = new ShowListVO();
            BeanUtils.copyProperties(show, vo);

            // 获取场次信息
            List<Session> sessions = sessionMapper.selectList(
                    new QueryWrapper<Session>().eq("show_id", show.getId()));
            vo.setSessions(sessions != null ? sessions : Collections.emptyList());

            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Page<Show> searchShows(String keyword, Integer page, Integer size) {
        // 参数校验
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }

        Page<Show> showPage = new Page<>(page, size);
        QueryWrapper<Show> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
                wrapper.like("name", keyword).or().like("description", keyword)
        );

        return showMapper.selectPage(showPage, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShowDetailVO getShowDetail(Long id) {
        // 参数校验
        if (id == null) {
            throw new IllegalArgumentException("演出 ID 不能为空");
        }

        Show show = showMapper.selectById(id);
        if (show == null) {
            throw new RuntimeException("演出不存在");
        }

        ShowDetailVO detailVO = new ShowDetailVO();
        detailVO.setShow(show);

        // 获取场次信息
        List<Session> sessions = sessionMapper.selectList(
                new QueryWrapper<Session>().eq("show_id", id));
        detailVO.setSessions(sessions != null ? sessions : Collections.emptyList());

        // 如果有场次，获取第一个场次的票档信息
        if (!sessions.isEmpty()) {
            Session firstSession = sessions.get(0);
            List<TicketType> ticketTypes = ticketTypeMapper.selectList(
                    new QueryWrapper<TicketType>().eq("session_id", firstSession.getId()));
            detailVO.setTicketTypes(ticketTypes != null ? ticketTypes : Collections.emptyList());
        } else {
            detailVO.setTicketTypes(Collections.emptyList());
        }

        return detailVO;
    }
}