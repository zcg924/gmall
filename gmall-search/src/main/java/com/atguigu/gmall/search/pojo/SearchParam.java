package com.atguigu.gmall.search.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author zcgstart
 * @create 2020-01-14 19:55
 */
@Data
public class SearchParam {
    //检索关键字
    private String key;
    //三级分类
    private Long[] catelog3;
    //品牌
    private Long[] brand;

    //价格区间
    private Double priceFrom;
    private Double priceTo;

    //检索的属性组合
    private List<String> props;
    //排序
    private String order;

    //页码
    private Integer pageNum = 1;
    private Integer pageSize = 64;
    //是否有货
    private Boolean store;





}
