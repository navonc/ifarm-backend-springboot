package com.ifarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.Farm;

import java.util.List;

/**
 * 农场服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IFarmService extends IService<Farm> {

    /**
     * 根据农场主ID查询农场列表
     * 
     * @param ownerId 农场主ID
     * @return 农场列表
     */
    List<Farm> getFarmsByOwnerId(Long ownerId);

    /**
     * 根据地区查询农场列表
     * 
     * @param province 省份
     * @param city 城市
     * @param district 区县
     * @return 农场列表
     */
    List<Farm> getFarmsByLocation(String province, String city, String district);

    /**
     * 查询正常状态的农场列表
     * 
     * @return 正常状态的农场列表
     */
    List<Farm> getActiveFarms();

    /**
     * 分页查询农场列表
     * 
     * @param page 分页参数
     * @param ownerId 农场主ID（可选）
     * @param name 农场名称（可选，模糊查询）
     * @param province 省份（可选）
     * @param city 城市（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<Farm> getFarmPage(Page<Farm> page, Long ownerId, String name, 
                           String province, String city, Integer status);

    /**
     * 创建农场
     * 
     * @param farm 农场信息
     * @return 创建结果
     */
    boolean createFarm(Farm farm);

    /**
     * 更新农场信息
     * 
     * @param farm 农场信息
     * @return 更新结果
     */
    boolean updateFarm(Farm farm);

    /**
     * 删除农场
     * 
     * @param farmId 农场ID
     * @return 删除结果
     */
    boolean deleteFarm(Long farmId);

    /**
     * 更新农场状态
     * 
     * @param farmId 农场ID
     * @param status 状态：0-禁用，1-正常，2-审核中
     * @return 操作结果
     */
    boolean updateFarmStatus(Long farmId, Integer status);

    /**
     * 审核农场
     * 
     * @param farmId 农场ID
     * @param approved 是否通过审核
     * @param remark 审核备注
     * @return 审核结果
     */
    boolean auditFarm(Long farmId, Boolean approved, String remark);

    /**
     * 获取农场详情
     * 
     * @param farmId 农场ID
     * @return 农场详情
     */
    Farm getFarmDetail(Long farmId);

    /**
     * 检查农场名称是否存在
     * 
     * @param name 农场名称
     * @param excludeId 排除的农场ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByName(String name, Long excludeId);

    /**
     * 统计农场主的农场数量
     * 
     * @param ownerId 农场主ID
     * @return 农场数量
     */
    int countFarmsByOwnerId(Long ownerId);

    /**
     * 获取推荐农场列表
     * 
     * @param limit 限制数量
     * @return 推荐农场列表
     */
    List<Farm> getRecommendedFarms(Integer limit);

    /**
     * 搜索农场（支持名称、地址模糊搜索）
     * 
     * @param keyword 搜索关键词
     * @param page 分页参数
     * @return 搜索结果
     */
    IPage<Farm> searchFarms(String keyword, Page<Farm> page);

    /**
     * 获取农场统计信息
     * 
     * @param ownerId 农场主ID（可选，为空时统计所有农场）
     * @return 统计信息
     */
    Object getFarmStatistics(Long ownerId);

    /**
     * 检查用户是否为农场主
     * 
     * @param userId 用户ID
     * @return 是否为农场主
     */
    boolean isFarmOwner(Long userId);

    /**
     * 获取附近的农场列表
     * 
     * @param latitude 纬度
     * @param longitude 经度
     * @param radius 半径（公里）
     * @param limit 限制数量
     * @return 附近农场列表
     */
    List<Farm> getNearbyFarms(Double latitude, Double longitude, Double radius, Integer limit);
}
