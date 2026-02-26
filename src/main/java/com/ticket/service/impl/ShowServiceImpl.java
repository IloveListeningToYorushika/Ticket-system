package com.ticket.service.impl;

//注入了三个Mapper，分别操作show，session，ticket_type三张表
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

    @Override//分页查询演出列表，支持按城市和分类筛选，并要求演出状态为“已开票”，返回的列表中每个演出对象会附带场次信息
    //是否不要求已开票会更好些？
    public Page<ShowListVO> getShowList(String city, String category, Integer page, Integer size, Long userId) {
        Page<Show> showPage = new Page<>(page, size);
        QueryWrapper<Show> queryWrapper = new QueryWrapper<>();

        if (!"全部".equals(category) && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }

        queryWrapper.eq("city", city).eq("status", 1); // 已开票的演出

        Page<Show> resultPage = showMapper.selectPage(showPage, queryWrapper);
        // TODO：所有从数据库中查询的数据都需要先判空

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

    @Override//根据关键词模糊搜索演出，可匹配演出名称或者描述
    public Page<Show> searchShows(String keyword, Integer page, Integer size) {
        Page<Show> showPage = new Page<>(page, size);
        QueryWrapper<Show> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", keyword).or().like("description", keyword);
        return showMapper.selectPage(showPage, queryWrapper);
    }//如果一次查询返回多个演出，就需要多次查询数据库；可以优化

    @Override//根据演出ID获取演出详情，包括演出基本信息，场次列表以及第一个场次的票档列表
    public ShowDetailVO getShowDetail(Long id) {
        Show show = showMapper.selectById(id);
        if (show == null) {
            // TODO:应该要报错throw new exception
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
    //如果演出有多个场次，或许可以让前端先选择场次，再展示该场次的票档。可以改进
}
//方法中未对传入参数进行非空校验，比如city，category可能为null，可能导致SQL异常。优化：在Service层或Controller层增加校验。
//getShowDetail返回null时，Controller层可能无法区分是“演出不存在”还是“系统错误。优化：抛出自定义异常或者使用Optional