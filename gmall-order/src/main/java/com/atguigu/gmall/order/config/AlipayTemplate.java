package com.atguigu.gmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private  String app_id = "2016092200568607";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDZ0p0b4Ieew3b35GREwbsK6/DuQ0AHJu3h5OQPjbMjI6jlTcBXSj/4LJ7wPihLLeAaAlMmzuslpgns9SL922HlAcif1gojY9hcHzdQ/UQjJ5l7nDUIIMCupAZUMMNb6bqhGAwVNtsfy8l8PgCcw7BnMW+21I/0CTVrGtA2AHeTYRxCzI4VQ5mjiyDC1YLUVBlb/1SKPrEpYku6uomc6WaxkO6a5t9qnPg61EgtBWmrDO2EZ2hBCJoJCkjrdfTC0cW3jQvCrdqB1vXo8Jb7K4fJ8hIZY2CYmCiCyWZkmfsGVbpMyFgBdbKT4R5odIST61e8m2D4cSWqX3uw9qi7u499AgMBAAECggEBAMIDEwbSfVmO1jdECX8oYzX+IzHFWpFEPluW6P9lgepJh9T95NSbANFGxSZM5szXhqUD7xZA9TcqZerWCR29OrukVBLxrUQKWZrr/vQ3MWpyigWMg7vCmHy1AiIY8Ni8HQwBhDmdC52NgOAVxavbrRKs+MkLGVEOprLIRi3jSp5MGsjaOuw6Mf50Uwue+XJ3KfwgVVaPqQkUs6S2ZuXFByzmzN9mNruQh2vRVPK7Tnm92JHtCGV29hw9wCNgnTskxcMSTmItQcMUrnhSWQqAWy6wuvtrxrSs7X6Ty7QS2BU4PlEEkp9VU1fYs5p6SZ6xG3laTSwbtESbQf7cPQhRbY0CgYEA9ZGSP+DiZe1NX4fGkIOLPc/+HCNQiIKEhB55K2kt5FuVtqKE0rrfsrRUzaQCFqJY22z0sYAd5MLwHoe1OcJ7/5hS3ungxj14PYvUUPS4SUWcv/Ly8foxanez95A6xpr4vODMsZUHQa4L3SUSSsc1zS6zf8sPgDQrlOe6OxnZsJsCgYEA4xNR9lv3x71+XHIaEW8L/76IvMS1FiVRMk0ZglIvsmA8JNXFhL4yjjKM0zUIP5sKUzf10LWvC0UcltKigbfCBzjsD7u0S+WT8zWkFJrKfhTaG/Tua7ao3hYqMozbqscDCOXS7VWmBEC5Yw2rYsCbKmKV/nQjmgyPc9Jcht+UxccCgYBQYJx5EheoexrHOP7SUGwkCE9JUHnMpMPRJtRMVDn0M9H2HI3TF92edY4IXTVdyTfCYpBZBPFD7lZ/97VmuXSyvV3qnqy21mnj4z/UW7VyLjnXTiMc9uJz0chzMJ88YH8n+4CkY34jjl9NvBOXrEq85RtVqdZiBTF6pLp9G8H4HwKBgQC741mXQkPElwu+TH87W6oPA8W6g/zehSQpPdhUl9J1guFhDSzxCDtmo3cYr7HSh2PEtfIErsBWz+m29jSWpsi6FyRkZEjr39tKi2Dn16ijKHfMWB8LK0mQ8cAasah1J1RggWHFFXBX5oI+7qmX/YymRoqbz9dBeREdobsDsm40MwKBgEAF1dMxF4ixTDJWy+s8axJRTBNhxX7tND2SJ38PiXVfAE+czBpdH/vy2CDk8fJMnhqWcnAhcqidorNv84qFggMbCBbyLb+1fD5ePz4yPxeMkygYcSV8LdR62bLNRcnXj21nqpi2u4f9au0pzvwZr9w7GeTpFIIwmy9Qd1sfwctH";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjGc/G6w/1NkX3j13KlKPcWc6tACV+hT55ep18JKZqYTwQpRbLwWQjB9h9KCFlVowOkr/o4cvgSct/3WBc+z1japySMXa/JFxEw3FWLjfvm8RQ3OS06STqGmyiNiJ7JaW82XD9TOo543OpofLQ288n/jeRYrGP8A+pmZaLboSKbBGXzkUF87yaX+sygC9GWS2soM0Fwm7DYTATEiIsRT/71PAyNdq9LaQOX8JtGsZ4FDmCTiS6C8Vf0lAEL6fG4LrPDodrYnPUjk5ZQYvUOAp4s5/0UslXybYFgLBIBjWv4ivkHMhHcjUQ3/bZrQQeFs3+Zz2rR/hvN0RcDmH2TxNdwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url;

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
