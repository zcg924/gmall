package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.pojo.SearchParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author zcgstart
 * @create 2020-01-14 20:27
 */
@Service
public class SearchService {
    @Autowired
    private RestHighLevelClient highLevelClient;

    public void search(SearchParam searchParam) throws IOException {
        SearchResponse searchResponse = this.highLevelClient.search(new SearchRequest(new String[]{"goods"}, buildDSL(searchParam)), RequestOptions.DEFAULT);
        System.out.println(searchResponse.toString());
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
        boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",key).operator(Operator.AND));
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
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
        Double priceFrom = searchParam.getPriceFrom();
        if(priceFrom != null){
            rangeQueryBuilder.gte(priceFrom);
        }
        Double priceTo = searchParam.getPriceTo();
        if(priceTo != null){
            rangeQueryBuilder.lte(priceTo);
        }
        boolQueryBuilder.filter(rangeQueryBuilder);

        //1.2.4 规格属性的过滤
        List<String> props = searchParam.getProps();
        if (!CollectionUtils.isEmpty(props)) {
            props.forEach(prop -> {
                String[] attr = StringUtils.split(prop, ":");
                if (attr != null && attr.length == 2) {
                    String attrId = attr[0];
                    String[] attrValues = StringUtils.split(attr[1], "-");
                    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                    boolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                    boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                    boolQueryBuilder.filter(QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None));
                }
            });
        }
        searchSourceBuilder.query(boolQueryBuilder);
        //2.构建排序
        String order = searchParam.getOrder();
        if(StringUtils.isNotBlank(order)){
            String[] orders = StringUtils.split(":");
            if(orders != null && orders.length == 2){
                String orderField = orders[0];
                String orderBy = orders[1];
                switch (orderField){
                    case "0": orderField = "_score"; break;
                    case "1": orderField = "sale";  break;
                    case "2": orderField = "price"; break;
                    default: orderField = "_score"; break;
                }
                searchSourceBuilder.sort(orderField, StringUtils.equals(orderBy,"asc") ? SortOrder.ASC : SortOrder.DESC);
            }
        }
        //3.构建分页
        Integer pageNum = searchParam.getPageNum();
        Integer pageSize = searchParam.getPageSize();
        searchSourceBuilder.from((pageNum - 1) * pageSize);
        searchSourceBuilder.size(pageSize);

        //4.构建高亮
        searchSourceBuilder.highlighter(new HighlightBuilder().field("skuTitle").preTags("<span style='color:red;'>").postTags("</span>"));
        //5.构建聚合
        //5.1 品牌的聚合
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("brandIdAgg").field("brandId").subAggregation(
                        AggregationBuilders.terms("brandNameAgg").field("brandName")
                )
        );
        //5.2 分类的聚合
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("categoryIdAgg").field("categoryId").subAggregation(
                        AggregationBuilders.terms("categoryNameAgg").field("categoryName")
                )
        );
        //5.3 规格属性的聚合
        searchSourceBuilder.aggregation(
                AggregationBuilders.nested("attrsAgg","attrs").subAggregation(
                        AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").subAggregation(
                                AggregationBuilders.terms("attrNameAgg").field("attrs.attrName")
                        ).subAggregation(
                                AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue")
                        )
                )
        );



        System.out.println(searchSourceBuilder.toString());
        return searchSourceBuilder;
    }
}
