package com.ifarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.Crop;

import java.util.List;

/**
 * 作物品种服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface ICropService extends IService<Crop> {

    /**
     * 根据分类ID查询作物列表
     * 
     * @param categoryId 分类ID
     * @return 作物列表
     */
    List<Crop> getCropsByCategoryId(Long categoryId);

    /**
     * 查询启用状态的作物列表
     * 
     * @return 启用的作物列表
     */
    List<Crop> getEnabledCrops();

    /**
     * 分页查询作物列表
     * 
     * @param page 分页参数
     * @param categoryId 分类ID（可选）
     * @param name 作物名称（可选，模糊查询）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<Crop> getCropPage(Page<Crop> page, Long categoryId, String name, Integer status);

    /**
     * 根据种植季节查询作物列表
     * 
     * @param plantingSeason 种植季节
     * @return 作物列表
     */
    List<Crop> getCropsByPlantingSeason(String plantingSeason);

    /**
     * 创建作物品种
     * 
     * @param crop 作物信息
     * @return 创建结果
     */
    boolean createCrop(Crop crop);

    /**
     * 更新作物品种信息
     * 
     * @param crop 作物信息
     * @return 更新结果
     */
    boolean updateCrop(Crop crop);

    /**
     * 删除作物品种
     * 
     * @param cropId 作物ID
     * @return 删除结果
     */
    boolean deleteCrop(Long cropId);

    /**
     * 启用/禁用作物品种
     * 
     * @param cropId 作物ID
     * @param status 状态：0-禁用，1-启用
     * @return 操作结果
     */
    boolean updateCropStatus(Long cropId, Integer status);

    /**
     * 批量启用/禁用作物品种
     * 
     * @param cropIds 作物ID列表
     * @param status 状态：0-禁用，1-启用
     * @return 操作结果
     */
    boolean batchUpdateCropStatus(List<Long> cropIds, Integer status);

    /**
     * 获取作物详情（包含分类信息）
     * 
     * @param cropId 作物ID
     * @return 作物详情
     */
    Crop getCropDetail(Long cropId);

    /**
     * 检查作物名称是否存在
     * 
     * @param name 作物名称
     * @param excludeId 排除的作物ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByName(String name, Long excludeId);

    /**
     * 获取热门作物列表
     * 
     * @param limit 限制数量
     * @return 热门作物列表
     */
    List<Crop> getPopularCrops(Integer limit);

    /**
     * 获取推荐作物列表（根据当前季节）
     * 
     * @param limit 限制数量
     * @return 推荐作物列表
     */
    List<Crop> getRecommendedCrops(Integer limit);

    /**
     * 搜索作物（支持名称、品种、描述模糊搜索）
     * 
     * @param keyword 搜索关键词
     * @param page 分页参数
     * @return 搜索结果
     */
    IPage<Crop> searchCrops(String keyword, Page<Crop> page);

    /**
     * 获取作物统计信息
     * 
     * @return 统计信息（总数、各分类数量等）
     */
    Object getCropStatistics();
}
