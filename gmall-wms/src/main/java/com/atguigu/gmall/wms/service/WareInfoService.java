package com.atguigu.gmall.wms.service;

import com.atguigu.wms.Api.entity.WareInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 仓库信息
 *
 * @author zcg
 * @email lxf@atguigu.com
 * @date 2020-01-02 20:01:38
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageVo queryPage(QueryCondition params);
}

