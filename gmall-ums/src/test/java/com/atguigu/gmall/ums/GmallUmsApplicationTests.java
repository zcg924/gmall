package com.atguigu.gmall.ums;

import com.atguigu.gmall.ums.util.SmsProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallUmsApplicationTests {

    @Autowired
    private SmsProperties smsProperties;

    @Test
    void contextLoads() {
        System.out.println(smsProperties.getSignname());
    }

}
