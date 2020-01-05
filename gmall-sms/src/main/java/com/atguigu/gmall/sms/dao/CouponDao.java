package com.atguigu.gmall.sms.dao;

import com.atguigu.gmall.sms.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zcg
 * @email lxf@atguigu.com
 * @date 2020-01-05 22:00:44
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
