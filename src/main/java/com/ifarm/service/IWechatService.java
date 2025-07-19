package com.ifarm.service;

import com.ifarm.dto.wechat.WechatUserInfo;

/**
 * 微信服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IWechatService {

    /**
     * 通过code获取微信用户信息
     * 
     * @param code 微信登录凭证
     * @return 微信用户信息
     */
    WechatUserInfo getWechatUserInfo(String code);

    /**
     * 解密微信用户数据
     * 
     * @param encryptedData 加密数据
     * @param iv 初始向量
     * @param sessionKey 会话密钥
     * @return 解密后的用户信息
     */
    WechatUserInfo decryptUserInfo(String encryptedData, String iv, String sessionKey);

    /**
     * 获取微信Access Token
     * 
     * @return Access Token
     */
    String getAccessToken();
}
