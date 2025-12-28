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
        Page<Show> showPage = new Page<>(page, size);
        QueryWrapper<Show> queryWrapper = new QueryWrapper<>();

        if (!"全部".equals(category) && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }

        queryWrapper.eq("city", city).eq("status", 1); // 已开票的演出

        Page<Show> resultPage = showMapper.selectPage(showPage, queryWrapper);

        Page<ShowListVO> voPage = new Page<>(page, size);
        List<ShowListVO> voList = resultPage.getRecords().stream().map(show -> {
            ShowListVO vo = new ShowListVO();
            BeanUtils.copyProperties(show, vo);

            // 获取场次信息
            List<Session> sessions = sessionMapper.selectList(
                    new QueryWrapper<Session>().eq("show_id", show.getId()));
            vo.setSessions(sessions);

            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        voPage.setTotal(resultPage.getTotal());

        return voPage;
    }

    @Override
    public Page<Show> searchShows(String keyword, Integer page, Integer size) {
        Page<Show> showPage = new Page<>(page, size);
        QueryWrapper<Show> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", keyword).or().like("description", keyword);
        return showMapper.selectPage(showPage, queryWrapper);
    }

    @Override
    public ShowDetailVO getShowDetail(Long id) {
        Show show = showMapper.selectById(id);
        if (show == null) {
            return null;
        }

        ShowDetailVO detailVO = new ShowDetailVO();
        detailVO.setShow(show);

        // 获取场次信息
        List<Session> sessions = sessionMapper.selectList(
                new QueryWrapper<Session>().eq("show_id", id));
        detailVO.setSessions(sessions);

        // 获取票档信息
        if (!sessions.isEmpty()) {
            List<TicketType> ticketTypes = ticketTypeMapper.selectList(
                    new QueryWrapper<TicketType>().eq("session_id", sessions.get(0).getId()));
            detailVO.setTicketTypes(ticketTypes);
        }

        return detailVO;
    }
}