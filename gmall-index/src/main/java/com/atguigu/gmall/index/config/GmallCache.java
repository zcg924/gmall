package com.atguigu.gmall.index.config;

import java.lang.annotation.*;

/**
 * @author zcgstart
 * @create 2020-03-03 21:49
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {

    //自定义缓存的key值前缀
    String value() default "";

    //自定义缓存的有效时间
    //单位是分钟
    int timeout() default 30;

    //防止雪崩，而设置随机值范围
    int bound() default 5;

    //自定义分布式锁的名称
    String lockName() default "lock";
}
