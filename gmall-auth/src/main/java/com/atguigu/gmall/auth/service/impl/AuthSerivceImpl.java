package com.atguigu.gmall.auth.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.exception.UmsException;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.auth.service.AuthService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zcgstart
 * @create 2020-03-11 0:18
 */
@Service
@EnableConfigurationProperties({JwtProperties.class})
public class AuthSerivceImpl implements AuthService{

    @Autowired
    private GmallUmsClient umsClient;
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public String accredit(String username, String password) {
        //1.远程调用feign查询接口
        Resp<MemberEntity> memberEntityResp = this.umsClient.query(username, password);
        MemberEntity memberEntity = memberEntityResp.getData();
        //2.判断用户是否为空
        if(memberEntity == null){
            throw new UmsException("用户名或密码错误");
        }
        try {
            //3.生成jwt
            Map<String, Object> map = new HashMap<>();
            map.put("id", memberEntity.getId());
            map.put("username", memberEntity.getUsername());
            String token = JwtUtils.generateToken(map, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpireTime());

            return token;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
