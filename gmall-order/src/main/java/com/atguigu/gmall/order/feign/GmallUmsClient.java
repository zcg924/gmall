package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zcgstart
 * @create 2020-03-11 0:22
 */
@FeignClient("ums-service")
public interface GmallUmsClient extends GmallUmsApi {
}
