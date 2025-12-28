package com.ticket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ticket.entity.Show;
import com.ticket.vo.ShowDetailVO;
import com.ticket.vo.ShowListVO;

public interface ShowService extends IService<Show> {

    Page<ShowListVO> getShowList(String city, String category, Integer page, Integer size, Long userId);

    Page<Show> searchShows(String keyword, Integer page, Integer size);

    ShowDetailVO getShowDetail(Long id);
}