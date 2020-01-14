package com.atguigu.gmall.wms.service;

import com.atguigu.wms.Api.entity.WareOrderTaskEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 库存工作单
 *
 * @author zcg
 * @email lxf@atguigu.com
 * @date 2020-01-02 20:01:38
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageVo queryPage(QueryCondition params);
}

