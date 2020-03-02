package com.atguigu.gmall.index.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author zcgstart
 * @create 2020-02-01 20:26
 */
@Service
public class IndexService {

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

    public List<CategoryVO> queryCategoriesWithSub(Long pid) {

        //获取缓存中的数据
        String cateJson = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid);
        //有，直接返回
        if(StringUtils.isNotBlank(cateJson)){
            return JSON.parseArray(cateJson, CategoryVO.class);
        }
        //没有，远程调用查询
        Resp<List<CategoryVO>> listResp = this.pmsClient.queryCategoriesWithSub(pid);
        List<CategoryVO> vos = listResp.getData();

        //查询完放入缓存
        this.stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(vos), 5 + new Random().nextInt(5), TimeUnit.DAYS);
        return vos;
    }
}
