package com.atguigu.gmall.cart.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zcgstart
 * @create 2020-03-11 20:57
 */
@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping()
    @ApiOperation("添加购物车")
    public Resp<Object> addCart(@RequestBody Cart cart){

        this.cartService.addCart(cart);

        return Resp.ok(null);
    }

    @GetMapping
    @ApiOperation("查询购物车")
    public Resp<List<Cart>> queryCarts(){
        List<Cart> carts = this.cartService.queryCarts();
        return Resp.ok(carts);
    }

    @ApiOperation("修改购物车")
    @PostMapping("update")
    public Resp<Object> updateNum(@RequestBody Cart cart){

        this.cartService.updateNum(cart);
        return Resp.ok(null);
    }

    @ApiOperation("选取购物车")
    @PostMapping("check")
    public Resp<Object> check(@RequestBody Cart cart){

        this.cartService.check(cart);
        return Resp.ok(null);
    }

    @PostMapping("delete")
    public Resp<Object> delete(@RequestParam("skuIds") List<Long> skuIds){

//        this.cartService.delete(skuId);
        return Resp.ok(null);
    }

    @ApiOperation("查询已选中的购物车")
    @GetMapping("{userId}")
    public List<Cart> queryCheckedCarts(@PathVariable("userId")Long userId){
        return this.cartService.queryCheckedCarts(userId);
    }

}
