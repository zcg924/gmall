package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zcgstart
 * @create 2020-03-05 21:51
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
