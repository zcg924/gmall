package com.atguigu.gmall.index.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zcgstart
 * @create 2020-02-01 20:28
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {

}
