package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.oms.vo.OrderItemVO;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.util.List;

/**
 * @author zcgstart
 * @create 2020-03-12 19:56
 */
@Data
public class OrderConfirmVO {

    private List<MemberReceiveAddressEntity> addresses;

    private List<OrderItemVO> orderItems;

    private Integer bounds; // 积分信息

    private String orderToken; // 防止重复提交
}
