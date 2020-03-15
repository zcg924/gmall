package com.atguigu.gmall.oms.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zcgstart
 * @create 2020-03-14 15:32
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
