package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.Map;


/**
 * 会员
 *
 * @author zcg
 * @email lxf@atguigu.com
 * @date 2020-03-09 23:12:04
 */
public interface MemberService extends IService<MemberEntity> {

    PageVo queryPage(QueryCondition params);


    Boolean checkData(String data, Integer type);


    void send(String phone, Map<String, Object> param);

    void register(MemberEntity member, String code);

    MemberEntity queryUser(String username, String password);
}

