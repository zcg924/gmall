package com.atguigu.sms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleVO {
    private Long skuId;
    //积分活动
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    /**
     * 优惠生效情况[1111（四个状态位，从右到左）;
     * 0 - 无优惠，成长积分是否赠送;
     * 1 - 无优惠，购物积分是否赠送;
     * 2 - 有优惠，成长积分是否赠送;
     * 3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
     */
    private List<Integer> work;

    // 满减活动
    private BigDecimal fullPrice;//满多少
    private BigDecimal reducePrice;//减多少
    private Integer fullAddOther;//满减是否可以叠加其他优惠

    private Integer fullCount;//满几件
    private BigDecimal discount;//打几折

    //是否叠加其他优惠[0-不可叠加，1-可叠加]
    private Integer ladderAddOther;
}
