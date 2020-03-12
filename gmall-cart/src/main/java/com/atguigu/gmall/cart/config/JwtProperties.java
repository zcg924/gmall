package com.atguigu.gmall.cart.config;

import com.atguigu.core.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @author zcgstart
 * @create 2020-03-11 0:01
 */
@ConfigurationProperties(prefix = "jwt.token")
@Data
public class JwtProperties {

    private String pubKeyPath;
    private String cookieName;
    private String userKey;

    private PublicKey publicKey;
    private Integer expireTime;

    @PostConstruct
    public void init(){
        try {
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


