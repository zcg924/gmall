package com.atguigu.gmall.pms.service;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.api.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * spu属性值
 *
 * @author zcg
 * @email lxf@atguigu.com
 * @date 2020-01-02 13:47:29
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageVo queryPage(QueryCondition params);

    List<ProductAttrValueEntity> querySearchAttrValue(Long spuId);
}

