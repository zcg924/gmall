package com.atguigu.gmall.oms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zcgstart
 * @create 2020-03-12 23:16
 */

public interface GmallOmsApi {

    @PostMapping("oms/order/{userId}")
    public Resp<OrderEntity> saveOrder(@RequestBody OrderSubmitVO orderSubmitVO, @PathVariable("userId")Long userId);
}
