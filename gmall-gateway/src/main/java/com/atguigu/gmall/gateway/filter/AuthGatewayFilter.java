package com.atguigu.gmall.gateway.filter;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.gateway.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author zcgstart
 * @create 2020-03-11 13:10
 */
@Component
@EnableConfigurationProperties({JwtProperties.class})
public class AuthGatewayFilter implements GatewayFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //1.获取jwt类型的token信息
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
            //如果cookie为空，或者cookie中不包含token
        if(CollectionUtils.isEmpty(cookies) || !cookies.containsKey(jwtProperties.getCookieName())){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();//响应结束
        }
        //获取token
        HttpCookie cookie = cookies.getFirst(this.jwtProperties.getCookieName());
        if(cookie == null){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();//响应结束
        }
        String token = cookie.getValue();
        //2.如果token为空
        if(StringUtils.isEmpty(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();//响应结束
        }

        //3.解析token信息
        try {
            //正常解析放行
            JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            //解析异常拦截
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();//响应结束
        }


        return chain.filter(exchange);
    }
}
