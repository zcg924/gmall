package com.atguigu.gmall.item.feign;

import com.atguigu.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zcgstart
 * @create 2020-03-05 21:52
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
