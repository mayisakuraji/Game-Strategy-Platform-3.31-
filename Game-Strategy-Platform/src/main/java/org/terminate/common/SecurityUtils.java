package org.terminate.common;

import org.springframework.util.DigestUtils;

public class SecurityUtils {
    /**
     * 采用 MD5 加盐加密
     * 对应要求：数据安全存储
     */
    public static String md5(String password) {
        String salt = "game_share_platform"; // 加上自定义盐值提高安全性
        return DigestUtils.md5DigestAsHex((password + salt).getBytes());
    }
}