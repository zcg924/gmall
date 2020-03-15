package com.atguigu.gmall.cart.pojo;

import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.sms.vo.ItemSaleVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zcgstart
 * @create 2020-03-11 19:24
 */
@Data
public class Cart {
    private Long skuId;
    private String skuTitle;
    private String image;
    private List<SkuSaleAttrValueEntity> saleAttrs; // 销售属性
    private BigDecimal price;
    private BigDecimal currentPrice; // 商品的当前价格
    private Integer count;
    private Boolean store = false; // 库存
    private Boolean check; // 选中状态
    private List<ItemSaleVO> sales; // 促销信息
}
