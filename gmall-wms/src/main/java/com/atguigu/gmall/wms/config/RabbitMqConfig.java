package com.atguigu.gmall.wms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zcgstart
 * @create 2020-03-14 22:30
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 延时队列
     * 延时时间：1min
     * 死信路由：order-exchange
     * 死信rountingkey：order.dead
     * x-dead-letter-exchange：出现dead letter之后将dead letter重新发送到指定exchange
     * x-dead-letter-routing-key：出现dead letter之后将dead letter重新按照指定的routing-key发送
     * @return
     */
    @Bean("ttl-queue")
    public Queue ttlQueue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "WMS-EXCHANGE");
        arguments.put("x-dead-letter-routing-key", "wms.dead");
        arguments.put("x-message-ttl", 60000);// 单位是毫秒
        return new Queue("WMS-TTL-QUEUE", true, false, false, arguments);
    }


    /**
     * 延时队列绑定到WMS-exchange路由
     * @return
     */
    @Bean("ttl-binding")
    public Binding ttlBinding(){

        return new Binding("WMS-TTL-QUEUE", Binding.DestinationType.QUEUE, "WMS-EXCHANGE", "wms.ttl", null);
    }

//    @Bean("dead-queue")
//    public Queue deadQueue(){
//
//        return new Queue("WMS-DEAD-QUEUE", true, false, false, null);
//    }


    /**
     * 死信队列绑定到order-exchange路由
     * @return
     */
//    @Bean("dead-binding")
//    public Binding deadBinding(){
//
//        return new Binding("WMS-DEAD-QUEUE", Binding.DestinationType.QUEUE, "WMS-EXCHANGE", "wms.dead", null);
//    }


}
