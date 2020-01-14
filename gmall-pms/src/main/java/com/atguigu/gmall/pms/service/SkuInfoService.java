package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.api.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;



/**
 * sku信息
 *
 * @author zcg
 * @email lxf@atguigu.com
 * @date 2020-01-02 13:47:29
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageVo queryPage(QueryCondition params);

}

