package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.cart.api.GmallCartApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zcgstart
 * @create 2020-03-05 21:52
 */
@FeignClient("cart-service")
public interface GmallCartClient extends GmallCartApi {
}
