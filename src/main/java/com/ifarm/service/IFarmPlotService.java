package com.ifarm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.FarmPlot;

import java.math.BigDecimal;
import java.util.List;

/**
 * 农场地块服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IFarmPlotService extends IService<FarmPlot> {

    /**
     * 根据农场ID查询地块列表
     * 
     * @param farmId 农场ID
     * @return 地块列表
     */
    List<FarmPlot> getPlotsByFarmId(Long farmId);

    /**
     * 根据农场ID查询可用地块列表
     * 
     * @param farmId 农场ID
     * @return 可用地块列表
     */
    List<FarmPlot> getAvailablePlotsByFarmId(Long farmId);

    /**
     * 根据土壤类型查询地块列表
     * 
     * @param soilType 土壤类型
     * @return 地块列表
     */
    List<FarmPlot> getPlotsBySoilType(String soilType);

    /**
     * 创建地块
     * 
     * @param farmPlot 地块信息
     * @return 创建结果
     */
    boolean createPlot(FarmPlot farmPlot);

    /**
     * 更新地块信息
     * 
     * @param farmPlot 地块信息
     * @return 更新结果
     */
    boolean updatePlot(FarmPlot farmPlot);

    /**
     * 删除地块
     * 
     * @param plotId 地块ID
     * @return 删除结果
     */
    boolean deletePlot(Long plotId);

    /**
     * 更新地块状态
     * 
     * @param plotId 地块ID
     * @param status 状态：0-禁用，1-可用，2-使用中
     * @return 操作结果
     */
    boolean updatePlotStatus(Long plotId, Integer status);

    /**
     * 批量更新地块状态
     * 
     * @param plotIds 地块ID列表
     * @param status 状态：0-禁用，1-可用，2-使用中
     * @return 操作结果
     */
    boolean batchUpdatePlotStatus(List<Long> plotIds, Integer status);

    /**
     * 获取地块详情
     * 
     * @param plotId 地块ID
     * @return 地块详情
     */
    FarmPlot getPlotDetail(Long plotId);

    /**
     * 检查地块名称在农场内是否存在
     * 
     * @param farmId 农场ID
     * @param name 地块名称
     * @param excludeId 排除的地块ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByNameInFarm(Long farmId, String name, Long excludeId);

    /**
     * 统计农场的地块数量
     * 
     * @param farmId 农场ID
     * @return 地块数量
     */
    int countPlotsByFarmId(Long farmId);

    /**
     * 统计农场的总地块面积
     * 
     * @param farmId 农场ID
     * @return 总面积
     */
    BigDecimal getTotalAreaByFarmId(Long farmId);

    /**
     * 统计农场可用地块面积
     * 
     * @param farmId 农场ID
     * @return 可用面积
     */
    BigDecimal getAvailableAreaByFarmId(Long farmId);

    /**
     * 检查地块是否可以删除（是否有关联的项目）
     * 
     * @param plotId 地块ID
     * @return 是否可以删除
     */
    boolean canDeletePlot(Long plotId);

    /**
     * 获取地块使用情况统计
     * 
     * @param farmId 农场ID
     * @return 使用情况统计
     */
    Object getPlotUsageStatistics(Long farmId);

    /**
     * 根据面积范围查询地块列表
     * 
     * @param farmId 农场ID
     * @param minArea 最小面积
     * @param maxArea 最大面积
     * @return 地块列表
     */
    List<FarmPlot> getPlotsByAreaRange(Long farmId, BigDecimal minArea, BigDecimal maxArea);

    /**
     * 检查用户是否有权限操作地块
     * 
     * @param userId 用户ID
     * @param plotId 地块ID
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, Long plotId);
}
