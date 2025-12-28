package com.ticket.controller;

import com.ticket.common.Result;
import com.ticket.entity.Show;
import com.ticket.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/show")
public class ShowController {

    @Autowired
    private ShowService showService;

    /**
     * 首页获取地区与分类演出列表
     */
    @GetMapping("/list")
    public Result getShowList(@RequestParam(defaultValue = "北京") String city,
                              @RequestParam(defaultValue = "") String category,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer size,
                              @RequestAttribute(required = false, value = "userId") Long userId) {
        return Result.success(showService.getShowList(city, category, page, size, userId));
    }

    /**
     * 搜索演出
     */
    @GetMapping("/search")
    public Result searchShows(@RequestParam String keyword,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(showService.searchShows(keyword, page, size));
    }

    /**
     * 获取演出详情
     */
    @GetMapping("/{id}")
    public Result getShowDetail(@PathVariable Long id) {
        return Result.success(showService.getShowDetail(id));
    }
}