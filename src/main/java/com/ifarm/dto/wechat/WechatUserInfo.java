package com.ifarm.dto.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信用户信息
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
public class WechatUserInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户openid
     */
    private String openid;

    /**
     * 用户unionid
     */
    private String unionid;

    /**
     * 会话密钥
     */
    @JsonProperty("session_key")
    private String sessionKey;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    @JsonProperty("avatar_url")
    private String avatarUrl;

    /**
     * 用户性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    private Integer gender;

    /**
     * 用户所在国家
     */
    private String country;

    /**
     * 用户所在省份
     */
    private String province;

    /**
     * 用户所在城市
     */
    private String city;

    /**
     * 显示 country，province，city 所用的语言
     */
    private String language;

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;
}
