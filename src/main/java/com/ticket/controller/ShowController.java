package com.ticket.controller;

import com.ticket.common.Result;
import com.ticket.common.context.UserContext;
import com.ticket.entity.Show;
import com.ticket.service.ShowService;
import com.ticket.vo.ShowDetailVO;
import com.ticket.vo.ShowListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@RequestMapping("/api/show")
public class ShowController {

    @Autowired
    private ShowService showService;

    /**
     * 首页获取地区与分类演出列表
     */
    @GetMapping("/list")
    public Result<Page<ShowListVO>> getShowList(@RequestParam(defaultValue = "北京") String city,
                                                @RequestParam(required = false, defaultValue = "") String category,
                                                @RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size) {
        try {
            Long userId = UserContext.getUserId();
            return Result.success(showService.getShowList(city, category, page, size, userId));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 搜索演出
     */
    @GetMapping("/search")
    public Result<Page<Show>> searchShows(@RequestParam String keyword,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        try {
            return Result.success(showService.searchShows(keyword, page, size));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取演出详情
     */
    @GetMapping("/{id}")
    public Result<ShowDetailVO> getShowDetail(@PathVariable Long id) {
        try {
            return Result.success(showService.getShowDetail(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}