package com.atguigu.gmall.search.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseVO;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author zcgstart
 * @create 2020-01-14 20:28
 */

@RestController
@RequestMapping("search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping
    public Resp<Object> search(SearchParam searchParam) throws IOException {

        SearchResponseVO searchResponseVO = this.searchService.search(searchParam);
        return Resp.ok(searchResponseVO);
    }
}
