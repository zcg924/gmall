package com.atguigu.gmall.wms.service;

import com.atguigu.wms.Api.entity.ShAreaEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 全国省市区信息
 *
 * @author zcg
 * @email lxf@atguigu.com
 * @date 2020-01-02 20:01:38
 */
public interface ShAreaService extends IService<ShAreaEntity> {

    PageVo queryPage(QueryCondition params);
}

