package com.atguigu.gmall.search.feign;

import com.atguigu.wms.Api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("wms-service")
public interface GmallWmsClient extends GmallWmsApi {
}
