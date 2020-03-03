package com.atguigu.gmall.index.config;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * @author zcgstart
 * @create 2020-03-03 21:57
 */
@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(com.atguigu.gmall.index.config.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        GmallCache gmallCache = method.getAnnotation(GmallCache.class);
        Class returnType = signature.getReturnType();
        List<Object> args =        Arrays.asList(joinPoint.getArgs());

        //获取缓存数据
        String prefix = gmallCache.value();
        String key = prefix + args;
        Object cache = getCache(returnType, key);
        if(cache != null){
            return cache;
        }

        //为空，加分布式锁
        String lockName = gmallCache.lockName();
        RLock fairLock = this.redissonClient.getFairLock(lockName + args);
        fairLock.lock();
        //判断缓存是否为空

        Object cache1 = getCache(returnType, key);
        if(cache1 != null){
            return cache1;
        }

        Object result = joinPoint.proceed(joinPoint.getArgs());

        //把数据放入缓存
        this.redisTemplate.opsForValue().set(key, JSON.toJSONString(result), gmallCache.timeout() + new Random().nextInt(gmallCache.bound()));
        //释放分布式锁
        fairLock.unlock();
        return result;
    }

    private Object getCache(Class returnType, String key) {
        String jsonString = this.redisTemplate.opsForValue().get(key);
        //判断数据是否为空
        if (StringUtils.isNotBlank(jsonString)) {
            return JSON.parseObject(jsonString, returnType);
        }
        return null;
    }
}
