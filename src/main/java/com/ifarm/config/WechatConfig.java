package com.ifarm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信配置
 *
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Configuration
public class WechatConfig {

    /**
     * 小程序配置
     */
    @Data
    @Configuration
    @ConfigurationProperties(prefix = "wechat.miniapp")
    public static class MiniAppConfig {
        /**
         * 小程序AppID
         */
        private String appId;

        /**
         * 小程序AppSecret
         */
        private String appSecret;

        /**
         * 微信API基础URL
         */
        private String apiUrl = "https://api.weixin.qq.com";

        /**
         * 获取access_token的URL
         */
        public String getAccessTokenUrl() {
            return apiUrl + "/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        }

        /**
         * 通过code获取session_key和openid的URL
         */
        public String getCode2SessionUrl(String code) {
            return apiUrl + "/sns/jscode2session?appid=" + appId + "&secret=" + appSecret + "&js_code=" + code + "&grant_type=authorization_code";
        }
    }

    /**
     * 微信支付配置
     */
    @Data
    @Configuration
    @ConfigurationProperties(prefix = "wechat.pay")
    public static class PayConfig {
        /**
         * 商户号
         */
        private String mchId;

        /**
         * 证书序列号
         */
        private String certSerialNo;

        /**
         * APIv3密钥
         */
        private String apiV3Key;

        /**
         * APIv2密钥
         */
        private String apiV2Key;

        /**
         * 支付回调通知URL
         */
        private String notifyUrl;

        /**
         * 微信支付API基础URL
         */
        private String payApiUrl = "https://api.mch.weixin.qq.com";
    }
}
