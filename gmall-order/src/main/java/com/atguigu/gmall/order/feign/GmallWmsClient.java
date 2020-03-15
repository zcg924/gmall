package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.wms.Api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zcgstart
 * @create 2020-03-05 21:53
 */
@FeignClient("wms-service")
public interface GmallWmsClient extends GmallWmsApi {
}
