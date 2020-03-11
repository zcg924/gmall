package com.atguigu.gmall.auth;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zcgstart
 * @create 2020-03-10 23:37
 */
public class JwtTest {

    private static final String pubKeyPath = "F:\\work\\RSA\\rsa.pub";

    private static final String priKeyPath = "F:\\work\\RSA\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwiZXhwIjoxNTgzODU1OTcyLCJ1c2VybmFtZSI6ImxpdXlhbiJ9.BIi4fheTUBXGc5VKtjwI3-_jksbT4LbF1K2yoirHfJgKUC9sn1VMiMu8MsDt7ZZfpsytzT2XUGHf1kGv7Ba3lhvyock5vAg61jOy9C5cDU2kWyffmvbk5ib3pKvjwODn_MsCZVxFBet2RiAJd-CyTcSTpZ8hpR140n3sDlYf8GLXXQVAjoxxkbhBnPsp1nT-OW63Mr4jb2S1SRW4Hukj9qaZTxkns9ch7Lq_nBmW_dRSjZJwxsCbyH9ddozZmGz9eslLo63jrTdoiFLhGGE34ZOYWPfR95p3xLO3fctZsJFUO6SMs4KY1K03FehaRsHfTFxd9U2kyOYt2fkj1PF05Q";
        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }

}
