package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.oms.api.GmallOmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zcgstart
 * @create 2020-03-14 15:52
 */
@FeignClient("oms-service")
public interface GmallOmsClient extends GmallOmsApi {
}
