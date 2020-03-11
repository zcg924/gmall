package com.atguigu.gmall.ums.service.impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.core.exception.RRException;
import com.atguigu.core.exception.UmsException;
import com.atguigu.core.utils.ExceptionUtils;
import com.atguigu.core.utils.FormUtils;
import com.atguigu.gmall.ums.util.SmsProperties;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private SmsProperties smsProperties;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean checkData(String data, Integer type) {

        QueryWrapper<MemberEntity> queryWrapper = new QueryWrapper<>();
        switch (type) {
            case 1:
                queryWrapper.eq("username", data);
                break;
            case 2:
                queryWrapper.eq("mobile", data);
                break;
            case 3:
                queryWrapper.eq("email", data);
                break;
            default:
                return null;
        }
        return this.count(queryWrapper) == 0;
    }

    @Override
    public void send(String phone, Map<String, Object> param) {

        //校验手机号码
        if (StringUtils.isEmpty(phone) || !FormUtils.isMobile(phone)) {
            log.error("请输入正确的手机号码");
            throw new RRException("手机号不正确");
        }

        DefaultProfile profile = DefaultProfile.getProfile(
                smsProperties.getRegionid(),
                smsProperties.getKeyid(),
                smsProperties.getKeysecret());
        IAcsClient client = new DefaultAcsClient(profile);

        //组装参数
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("RegionId", smsProperties.getRegionid());
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", smsProperties.getSignname());
        request.putQueryParameter("TemplateCode", smsProperties.getTemplatecode());

        //将包含验证码的map集合组装成json字符串
        Gson gson = new Gson();
        request.putQueryParameter("TemplateParam", gson.toJson(param));

        try {
            //发送信息
            CommonResponse response = client.getCommonResponse(request);
            //得到json字符串响应结果
            String data = response.getData();

            //解析json字符串
            HashMap<String, String> map = gson.fromJson(data, HashMap.class);
            String code = map.get("Code");
            String message = map.get("message");

            //业务限流
            if ("isv.BUSINESS_LIMIT_CONTROL".equals(code)) {
                log.error("短信发送过于频繁 " + "【code】" + code + ", 【message】" + message);
                throw new RRException("信息发送过于频繁");
            }

            if (!"OK".equals(code)) {
                log.error("短信发送失败 " + " - code: " + code + ", message: " + message);
                throw new RRException("短信发送失败");
            }

        } catch (ServerException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new RRException("短信发送失败");
        } catch (ClientException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new RRException("短信发送失败");
        }
    }

    @Override
    public void register(MemberEntity member, String code) {
        //1.验证验证码
        String rCode = this.redisTemplate.opsForValue().get(member.getMobile());
        if(!StringUtils.equals(code, rCode)){
            throw new UmsException("验证码错误");
        }
        //2.生成盐
        String salt = UUID.randomUUID().toString().substring(0, 6);
        //3.加盐加密
        member.setPassword(DigestUtils.md5Hex(member.getPassword() + salt));
        //4.存储用户
        member.setLevelId(1l);
        member.setSourceType(1);
        member.setIntegration(1000);
        member.setGrowth(1000);
        member.setStatus(1);
        member.setCreateTime(new Date());
        this.save(member);
        //5.删除验证码
        redisTemplate.delete(member.getMobile());

    }

    @Override
    public MemberEntity queryUser(String username, String password) {
        //1.根据用户名查询用户信息
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", username));
        //2.判断用户是否为空
        if(memberEntity == null){
            return memberEntity;
        }
        //3.获取盐对用户输入的密码加盐加密
        String salt = memberEntity.getSalt();
        password = DigestUtils.md5Hex(password + salt);
        //4.与数据库中的密码比对
        if(!StringUtils.equals(password, memberEntity.getPassword())){
            return null;
        }
        return memberEntity;
    }
}
