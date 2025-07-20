package com.ifarm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.SystemConfig;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface ISystemConfigService extends IService<SystemConfig> {

    /**
     * 根据配置键获取配置值
     * 
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值，如果不存在返回默认值
     * 
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 获取字符串类型配置值
     * 
     * @param configKey 配置键
     * @return 字符串值
     */
    String getStringValue(String configKey);

    /**
     * 获取数字类型配置值
     * 
     * @param configKey 配置键
     * @return 数字值
     */
    BigDecimal getNumberValue(String configKey);

    /**
     * 获取布尔类型配置值
     * 
     * @param configKey 配置键
     * @return 布尔值
     */
    Boolean getBooleanValue(String configKey);

    /**
     * 获取JSON类型配置值并解析为Map
     * 
     * @param configKey 配置键
     * @return Map对象
     */
    Map<String, Object> getJsonValue(String configKey);

    /**
     * 设置配置值
     * 
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 设置结果
     */
    boolean setConfigValue(String configKey, String configValue);

    /**
     * 批量设置配置值
     * 
     * @param configMap 配置键值对
     * @return 设置结果
     */
    boolean batchSetConfigValues(Map<String, String> configMap);

    /**
     * 创建新配置项
     * 
     * @param systemConfig 配置信息
     * @return 创建结果
     */
    boolean createConfig(SystemConfig systemConfig);

    /**
     * 更新配置项
     * 
     * @param systemConfig 配置信息
     * @return 更新结果
     */
    boolean updateConfig(SystemConfig systemConfig);

    /**
     * 删除配置项
     * 
     * @param configKey 配置键
     * @return 删除结果
     */
    boolean deleteConfig(String configKey);

    /**
     * 获取所有配置项
     * 
     * @return 配置列表
     */
    List<SystemConfig> getAllConfigs();

    /**
     * 获取配置项Map（键值对形式）
     * 
     * @return 配置Map
     */
    Map<String, String> getConfigMap();

    /**
     * 检查配置键是否存在
     * 
     * @param configKey 配置键
     * @return 是否存在
     */
    boolean existsByConfigKey(String configKey);

    /**
     * 刷新配置缓存
     */
    void refreshConfigCache();

    /**
     * 获取系统基本信息配置
     * 
     * @return 系统基本信息
     */
    Map<String, Object> getSystemInfo();

    /**
     * 获取微信小程序配置
     * 
     * @return 微信配置
     */
    Map<String, String> getWechatConfig();

    /**
     * 获取支付相关配置
     * 
     * @return 支付配置
     */
    Map<String, String> getPaymentConfig();
}
