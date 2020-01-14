package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.pojo.SearchParam;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @author zcgstart
 * @create 2020-01-14 20:27
 */
public class SearchService {
    @Autowired
    private RestHighLevelClient highLevelClient;

    public void search(SearchParam searchParam) throws IOException {
        SearchResponse searchResponse = this.highLevelClient.search(new SearchRequest(new String[]{"goods"}, buildDSL()), RequestOptions.DEFAULT);
        System.out.println(searchResponse);
    }

    private SearchSourceBuilder buildDSL(SearchParam searchParam){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String key = searchParam.getKey();

        if (StringUtils.isEmpty(key)){
            //显示默认的商品列表
            return searchSourceBuilder;
        }
        //1.构建查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1.1构建匹配查询
        boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle","key").operator(Operator.AND));
        //1.2构建过滤条件
        //1.2.1 品牌的过滤
        Long[] brandIds = searchParam.getBrand();
        if (brandIds != null && brandIds.length != 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",brandIds));
        }
        //1.2.2 分类的过滤
        Long[] catelog3 = searchParam.getCatelog3();
        if (catelog3 != null && catelog3.length != 0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("categoryId",catelog3));
        }
        //1.2.3 价格区间的过滤
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery();
        Double priceFrom = searchParam.getPriceFrom();
        if(priceFrom != null){
            rangeQueryBuilder.gte(priceFrom);
        }
        Double priceTo = searchParam.getPriceTo();
        if(priceTo != null){
            rangeQueryBuilder.lte(priceTo);
        }

        //1.2.4 规格属性的过滤
        //2.构建排序

        //3.构建分页

        //4.构建高亮

        //5.构建聚合
        //5.1 品牌的聚合
        //5.2 分类的聚合



        System.out.println(searchSourceBuilder.toString());
        return searchSourceBuilder;
    }
}
