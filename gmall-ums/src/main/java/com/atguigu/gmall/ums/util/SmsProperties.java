package com.atguigu.gmall.ums.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zcgstart
 * @create 2020-03-05 9:38
 */
@Data
@Component
//注意prefix要写到最后一个 "." 符号之前
@ConfigurationProperties(prefix="aliyun.sms")
public class SmsProperties {

    private String regionid;
    private String keyid;
    private String keysecret;
    private String templatecode;
    private String signname;
}
