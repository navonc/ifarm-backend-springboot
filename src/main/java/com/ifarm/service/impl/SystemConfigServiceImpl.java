package com.ifarm.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.SystemConfig;
import com.ifarm.mapper.SystemConfigMapper;
import com.ifarm.service.ISystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统配置服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements ISystemConfigService {

    private final SystemConfigMapper systemConfigMapper;
    
    /**
     * 本地缓存
     */
    private final Map<String, String> configCache = new ConcurrentHashMap<>();

    @Override
    @Cacheable(value = "systemConfig", key = "#configKey")
    public String getConfigValue(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            throw new BusinessException("配置键不能为空");
        }
        
        log.debug("获取系统配置: {}", configKey);
        try {
            // 先从本地缓存获取
            String cachedValue = configCache.get(configKey);
            if (cachedValue != null) {
                return cachedValue;
            }
            
            // 从数据库获取
            SystemConfig config = systemConfigMapper.selectByConfigKey(configKey);
            if (config != null) {
                String value = config.getConfigValue();
                configCache.put(configKey, value);
                return value;
            }
            
            log.warn("未找到配置项: {}", configKey);
            return null;
        } catch (Exception e) {
            log.error("获取系统配置失败，配置键: {}", configKey, e);
            throw new BusinessException("获取系统配置失败");
        }
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return value != null ? value : defaultValue;
    }

    @Override
    public String getStringValue(String configKey) {
        return getConfigValue(configKey);
    }

    @Override
    public BigDecimal getNumberValue(String configKey) {
        String value = getConfigValue(configKey);
        if (!StringUtils.hasText(value)) {
            return null;
        }
        
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            log.error("配置值不是有效的数字: {} = {}", configKey, value);
            throw new BusinessException("配置值格式错误");
        }
    }

    @Override
    public Boolean getBooleanValue(String configKey) {
        String value = getConfigValue(configKey);
        if (!StringUtils.hasText(value)) {
            return null;
        }
        
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

    @Override
    public Map<String, Object> getJsonValue(String configKey) {
        String value = getConfigValue(configKey);
        if (!StringUtils.hasText(value)) {
            return new HashMap<>();
        }
        
        try {
            return JSON.parseObject(value, Map.class);
        } catch (JSONException e) {
            log.error("配置值不是有效的JSON: {} = {}", configKey, value);
            throw new BusinessException("配置值JSON格式错误");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "systemConfig", key = "#configKey")
    public boolean setConfigValue(String configKey, String configValue) {
        if (!StringUtils.hasText(configKey)) {
            throw new BusinessException("配置键不能为空");
        }
        
        log.info("设置系统配置: {} = {}", configKey, configValue);
        try {
            int result = systemConfigMapper.updateValueByKey(configKey, configValue);
            if (result > 0) {
                // 更新本地缓存
                configCache.put(configKey, configValue);
                log.info("系统配置更新成功");
                return true;
            } else {
                log.warn("配置项不存在，尝试创建新配置: {}", configKey);
                SystemConfig config = new SystemConfig();
                config.setConfigKey(configKey);
                config.setConfigValue(configValue);
                config.setConfigType("string");
                return createConfig(config);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("设置系统配置失败", e);
            throw new BusinessException("设置系统配置失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSetConfigValues(Map<String, String> configMap) {
        if (configMap == null || configMap.isEmpty()) {
            throw new BusinessException("配置数据不能为空");
        }
        
        log.info("批量设置系统配置，数量: {}", configMap.size());
        try {
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                setConfigValue(entry.getKey(), entry.getValue());
            }
            log.info("批量设置系统配置成功");
            return true;
        } catch (Exception e) {
            log.error("批量设置系统配置失败", e);
            throw new BusinessException("批量设置系统配置失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createConfig(SystemConfig systemConfig) {
        if (systemConfig == null || !StringUtils.hasText(systemConfig.getConfigKey())) {
            throw new BusinessException("配置信息不完整");
        }
        
        log.info("创建系统配置: {}", systemConfig.getConfigKey());
        try {
            // 验证配置键唯一性
            if (existsByConfigKey(systemConfig.getConfigKey())) {
                throw new BusinessException("配置键已存在");
            }
            
            // 设置默认值
            if (!StringUtils.hasText(systemConfig.getConfigType())) {
                systemConfig.setConfigType("string");
            }
            
            boolean result = save(systemConfig);
            if (result) {
                // 更新本地缓存
                configCache.put(systemConfig.getConfigKey(), systemConfig.getConfigValue());
                log.info("系统配置创建成功");
            } else {
                log.error("系统配置创建失败");
                throw new BusinessException("系统配置创建失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建系统配置失败", e);
            throw new BusinessException("创建系统配置失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "systemConfig", key = "#systemConfig.configKey")
    public boolean updateConfig(SystemConfig systemConfig) {
        if (systemConfig == null || systemConfig.getId() == null) {
            throw new BusinessException("配置信息不完整");
        }
        
        log.info("更新系统配置: ID={}, Key={}", systemConfig.getId(), systemConfig.getConfigKey());
        try {
            // 验证配置是否存在
            SystemConfig existingConfig = getById(systemConfig.getId());
            if (existingConfig == null) {
                throw new BusinessException("配置不存在");
            }
            
            boolean result = updateById(systemConfig);
            if (result) {
                // 更新本地缓存
                configCache.put(systemConfig.getConfigKey(), systemConfig.getConfigValue());
                log.info("系统配置更新成功");
            } else {
                log.error("系统配置更新失败");
                throw new BusinessException("系统配置更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新系统配置失败", e);
            throw new BusinessException("更新系统配置失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "systemConfig", key = "#configKey")
    public boolean deleteConfig(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            throw new BusinessException("配置键不能为空");
        }
        
        log.info("删除系统配置: {}", configKey);
        try {
            LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemConfig::getConfigKey, configKey);
            
            boolean result = remove(wrapper);
            if (result) {
                // 清除本地缓存
                configCache.remove(configKey);
                log.info("系统配置删除成功");
            } else {
                log.error("系统配置删除失败");
                throw new BusinessException("系统配置删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除系统配置失败", e);
            throw new BusinessException("删除系统配置失败");
        }
    }

    @Override
    public List<SystemConfig> getAllConfigs() {
        log.debug("获取所有系统配置");
        try {
            return list();
        } catch (Exception e) {
            log.error("获取所有系统配置失败", e);
            throw new BusinessException("获取系统配置失败");
        }
    }

    @Override
    public Map<String, String> getConfigMap() {
        log.debug("获取系统配置Map");
        try {
            List<SystemConfig> configs = getAllConfigs();
            Map<String, String> configMap = new HashMap<>();
            for (SystemConfig config : configs) {
                configMap.put(config.getConfigKey(), config.getConfigValue());
            }
            return configMap;
        } catch (Exception e) {
            log.error("获取系统配置Map失败", e);
            throw new BusinessException("获取系统配置失败");
        }
    }

    @Override
    public boolean existsByConfigKey(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return false;
        }
        
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        return count(wrapper) > 0;
    }

    @Override
    @CacheEvict(value = "systemConfig", allEntries = true)
    public void refreshConfigCache() {
        log.info("刷新系统配置缓存");
        configCache.clear();
        
        // 重新加载所有配置到本地缓存
        List<SystemConfig> configs = getAllConfigs();
        for (SystemConfig config : configs) {
            configCache.put(config.getConfigKey(), config.getConfigValue());
        }
        log.info("系统配置缓存刷新完成，加载{}个配置项", configs.size());
    }

    @Override
    public Map<String, Object> getSystemInfo() {
        log.debug("获取系统基本信息配置");
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("siteName", getConfigValue("site_name", "iFarm智慧农场"));
        systemInfo.put("siteLogo", getConfigValue("site_logo", "/images/logo.png"));
        systemInfo.put("version", getConfigValue("system_version", "1.0.0"));
        systemInfo.put("copyright", getConfigValue("copyright", "© 2024 iFarm"));
        return systemInfo;
    }

    @Override
    public Map<String, String> getWechatConfig() {
        log.debug("获取微信小程序配置");
        Map<String, String> wechatConfig = new HashMap<>();
        wechatConfig.put("appId", getConfigValue("wechat_appid", ""));
        wechatConfig.put("secret", getConfigValue("wechat_secret", ""));
        return wechatConfig;
    }

    @Override
    public Map<String, String> getPaymentConfig() {
        log.debug("获取支付相关配置");
        Map<String, String> paymentConfig = new HashMap<>();
        paymentConfig.put("defaultDeliveryFee", getConfigValue("default_delivery_fee", "15.00"));
        paymentConfig.put("freeDeliveryAmount", getConfigValue("free_delivery_amount", "100.00"));
        paymentConfig.put("alipayAppId", getConfigValue("alipay_appid", ""));
        return paymentConfig;
    }
}
