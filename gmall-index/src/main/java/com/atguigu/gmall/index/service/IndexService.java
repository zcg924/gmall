package com.atguigu.gmall.index.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.config.GmallCache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author zcgstart
 * @create 2020-02-01 20:26
 */
@Service
public class IndexService {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private GmallPmsClient pmsClient;

    private static final String KEY_PREFIX = "index:cates:";

    public List<CategoryEntity> queryLvl1Categories() {
        Resp<List<CategoryEntity>> categoriesResp = this.pmsClient.queryCategoriesByLevelOrPid(1, null);
        List<CategoryEntity> categoryEntities = categoriesResp.getData();
        return categoryEntities;
    }
    @GmallCache(value = "index:cates:", timeout = 7200, bound = 100, lockName = "lock")
    public List<CategoryVO> queryCategoriesWithSub(Long pid) {

//        //获取缓存中的数据
//        String cateJson = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid);
//        //有，直接返回
//        if(StringUtils.isNotBlank(cateJson)){
//            return JSON.parseArray(cateJson, CategoryVO.class);
//        }
//
//        RLock lock = this.redissonClient.getLock("lock" + pid);
//        lock.lock();
//
//        String cateJson2 = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid);
//        //有，直接返回
//        if(StringUtils.isNotBlank(cateJson2)){
//            return JSON.parseArray(cateJson2, CategoryVO.class);
//        }

        //没有，远程调用查询
        Resp<List<CategoryVO>> listResp = this.pmsClient.queryCategoriesWithSub(pid);
        List<CategoryVO> vos = listResp.getData();

//        //查询完放入缓存
//        this.stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(vos), 5 + new Random().nextInt(5), TimeUnit.DAYS);
//        lock.unlock();

        return vos;
    }

    public void testLock(){
        //1.从redis中获取锁,setnx
        String uuid = UUID.randomUUID().toString();
        Boolean lock = this.stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 2, TimeUnit.SECONDS);
        if(lock){
            //查询redis中的值
            String value = this.stringRedisTemplate.opsForValue().get("num");
            if(StringUtils.isNotBlank(value)){
                return ;
            }
            // 有值就转成成int
            int num = Integer.parseInt(value);
            // 把redis中的num值+1
            this.stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));
            // 2. 释放锁 del
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            this.stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("lock"), Arrays.asList(uuid));
        }else {
            // 3. 每隔1秒钟回调一次，再次尝试获取锁
            try {
                Thread.sleep(500);
                testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
