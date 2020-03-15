package com.atguigu.gmall.wms.Listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zcgstart
 * @create 2020-03-14 20:19
 */
@Component
public class StockListener {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private WareSkuDao wareSkuDao;

    private static final String KEY_PREFIX = "wms:stock:";

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "STOCK-UNLOCK-QUEUE", durable = "true"),
            exchange = @Exchange(value = "WMS-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"stock.unlock", "wms.dead"}
    ))
    public void unlock(String orderToken){
        String json = this.redisTemplate.opsForValue().get(KEY_PREFIX + orderToken);
        if (StringUtils.isEmpty(json)) {
            return ;
        }
        // 反序列化锁定库存信息
        List<SkuLockVO> skuLockVOS = JSON.parseArray(json, SkuLockVO.class);
        skuLockVOS.forEach(skuLockVO -> {
            this.wareSkuDao.unLock(skuLockVO.getWareSkuId(), skuLockVO.getCount());
            this.redisTemplate.delete(KEY_PREFIX + orderToken);
        });
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "STOCK-MINUS-QUEUE", durable = "true"),
            exchange = @Exchange(value = "WMS-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"stock.minus"}
    ))
    public void minus(String orderToken){
        // 获取锁定库存信息
        String json = this.redisTemplate.opsForValue().get(KEY_PREFIX + orderToken);
        if (StringUtils.isEmpty(json)) {
            return ;
        }
        // 反序列化锁定库存信息
        List<SkuLockVO> skuLockVOS = JSON.parseArray(json, SkuLockVO.class);
        skuLockVOS.forEach(skuLockVO -> {

            this.wareSkuDao.minus(skuLockVO.getWareSkuId(), skuLockVO.getCount());

            this.redisTemplate.delete(KEY_PREFIX + orderToken);
        });
    }


}
