package com.ifarm.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.common.result.ResultCode;
import com.ifarm.config.WechatConfig;
import com.ifarm.dto.wechat.WechatUserInfo;
import com.ifarm.service.IWechatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 微信服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatServiceImpl implements IWechatService {

    private final WechatConfig.MiniAppConfig miniAppConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public WechatUserInfo getWechatUserInfo(String code) {
        try {
            String url = miniAppConfig.getCode2SessionUrl(code);
            log.info("调用微信接口获取用户信息: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();
            
            log.info("微信接口响应: {}", responseBody);
            
            WechatUserInfo userInfo = JSON.parseObject(responseBody, WechatUserInfo.class);
            
            // 检查是否有错误
            if (userInfo.getErrcode() != null && userInfo.getErrcode() != 0) {
                log.error("微信接口返回错误: errcode={}, errmsg={}", userInfo.getErrcode(), userInfo.getErrmsg());
                throw new BusinessException(ResultCode.WECHAT_API_ERROR, "微信登录失败: " + userInfo.getErrmsg());
            }
            
            // 检查必要字段
            if (userInfo.getOpenid() == null) {
                log.error("微信接口未返回openid");
                throw new BusinessException(ResultCode.WECHAT_API_ERROR, "微信登录失败: 未获取到用户标识");
            }
            
            return userInfo;
            
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            throw new BusinessException(ResultCode.WECHAT_API_ERROR, "微信登录失败: " + e.getMessage());
        }
    }

    @Override
    public WechatUserInfo decryptUserInfo(String encryptedData, String iv, String sessionKey) {
        // TODO: 实现微信用户数据解密
        // 这里需要使用AES解密算法解密微信返回的加密用户数据
        // 由于涉及到复杂的加密解密逻辑，暂时返回空实现
        log.warn("微信用户数据解密功能暂未实现");
        return new WechatUserInfo();
    }

    @Override
    public String getAccessToken() {
        try {
            String url = miniAppConfig.getAccessTokenUrl();
            log.info("获取微信Access Token: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();
            
            log.info("微信Access Token响应: {}", responseBody);
            
            // 解析响应获取access_token
            // 这里简化处理，实际应该解析JSON并缓存token
            return responseBody;
            
        } catch (Exception e) {
            log.error("获取微信Access Token异常", e);
            throw new BusinessException(ResultCode.WECHAT_API_ERROR, "获取微信Access Token失败: " + e.getMessage());
        }
    }
}
