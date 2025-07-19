package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifarm.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统配置Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置键查询配置信息
     * 
     * @param configKey 配置键
     * @return 配置信息
     */
    SystemConfig selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 根据配置键更新配置值
     * 
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 更新行数
     */
    int updateValueByKey(@Param("configKey") String configKey, @Param("configValue") String configValue);
}
