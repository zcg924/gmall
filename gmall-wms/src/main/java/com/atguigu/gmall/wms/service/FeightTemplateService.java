package com.atguigu.gmall.wms.service;

import com.atguigu.wms.Api.entity.FeightTemplateEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 运费模板
 *
 * @author zcg
 * @email lxf@atguigu.com
 * @date 2020-01-02 20:01:38
 */
public interface FeightTemplateService extends IService<FeightTemplateEntity> {

    PageVo queryPage(QueryCondition params);
}

