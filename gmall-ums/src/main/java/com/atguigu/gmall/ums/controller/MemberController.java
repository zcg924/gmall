package com.atguigu.gmall.ums.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.RandomUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;


/**
 * 会员
 *
 * @author zcg
 * @email lxf@atguigu.com
 * @date 2020-03-09 23:12:04
 */
@Api(tags = "会员 管理")
@RestController
@RequestMapping("ums/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private StringRedisTemplate redisTemplate;


    @ApiOperation("根据用户名和密码查询指定用户")
    @GetMapping("query")
    public Resp<MemberEntity> query(@RequestParam("username")String username, @RequestParam("password")String password){
        MemberEntity member = this.memberService.queryUser(username, password);
        return Resp.ok(member);
    }


    @ApiOperation("用户注册")
    @PostMapping("register")
    public Resp<Object> register(MemberEntity member, @RequestParam("code")String code){

        this.memberService.register(member, code);
        return Resp.ok(null);
    }

    @ApiOperation("验证码发送")
    @PostMapping("code/{phone}")
    public Resp<Object> getCode(@PathVariable("phone")String phone){
        //生成验证码
        String code = RandomUtils.getFourBitRandom();
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);

        //发送短信验证码
        memberService.send(phone, param);

        //将验证码存入redis缓存
        redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

        return Resp.ok(null).msg("短信发送成功");
    }



    @ApiOperation("账号检验")
    @GetMapping("check/{data}/{type}")
    public Resp<Boolean> checkData(@PathVariable("data")String data, @PathVariable("type")Integer type){
        Boolean b = memberService.checkData(data,type);
        return Resp.ok(b);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ums:member:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = memberService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('ums:member:info')")
    public Resp<MemberEntity> info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return Resp.ok(member);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ums:member:save')")
    public Resp<Object> save(@RequestBody MemberEntity member){
		memberService.save(member);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ums:member:update')")
    public Resp<Object> update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ums:member:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
