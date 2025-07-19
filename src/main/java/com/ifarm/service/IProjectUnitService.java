package com.ifarm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.ProjectUnit;

import java.util.List;

/**
 * 项目单元服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IProjectUnitService extends IService<ProjectUnit> {

    /**
     * 根据项目ID查询单元列表
     * 
     * @param projectId 项目ID
     * @return 单元列表
     */
    List<ProjectUnit> getUnitsByProjectId(Long projectId);

    /**
     * 根据项目ID查询可认养单元列表
     * 
     * @param projectId 项目ID
     * @return 可认养单元列表
     */
    List<ProjectUnit> getAvailableUnitsByProjectId(Long projectId);

    /**
     * 根据项目ID和单元状态查询单元列表
     * 
     * @param projectId 项目ID
     * @param unitStatus 单元状态
     * @return 单元列表
     */
    List<ProjectUnit> getUnitsByProjectIdAndStatus(Long projectId, Integer unitStatus);

    /**
     * 创建项目单元
     * 
     * @param projectUnit 单元信息
     * @return 创建结果
     */
    boolean createUnit(ProjectUnit projectUnit);

    /**
     * 批量创建项目单元
     * 
     * @param projectId 项目ID
     * @param unitCount 单元数量
     * @return 创建结果
     */
    boolean batchCreateUnits(Long projectId, Integer unitCount);

    /**
     * 更新单元信息
     * 
     * @param projectUnit 单元信息
     * @return 更新结果
     */
    boolean updateUnit(ProjectUnit projectUnit);

    /**
     * 删除单元
     * 
     * @param unitId 单元ID
     * @return 删除结果
     */
    boolean deleteUnit(Long unitId);

    /**
     * 更新单元状态
     * 
     * @param unitId 单元ID
     * @param unitStatus 单元状态
     * @return 操作结果
     */
    boolean updateUnitStatus(Long unitId, Integer unitStatus);

    /**
     * 批量更新单元状态
     * 
     * @param unitIds 单元ID列表
     * @param unitStatus 新状态
     * @return 更新结果
     */
    boolean batchUpdateUnitStatus(List<Long> unitIds, Integer unitStatus);

    /**
     * 获取单元详情
     * 
     * @param unitId 单元ID
     * @return 单元详情
     */
    ProjectUnit getUnitDetail(Long unitId);

    /**
     * 根据项目ID和单元编号查询单元
     * 
     * @param projectId 项目ID
     * @param unitNumber 单元编号
     * @return 单元信息
     */
    ProjectUnit getUnitByProjectIdAndNumber(Long projectId, String unitNumber);

    /**
     * 检查单元编号在项目内是否存在
     * 
     * @param projectId 项目ID
     * @param unitNumber 单元编号
     * @param excludeId 排除的单元ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByNumberInProject(Long projectId, String unitNumber, Long excludeId);

    /**
     * 统计项目的单元数量
     * 
     * @param projectId 项目ID
     * @return 单元数量
     */
    int countUnitsByProjectId(Long projectId);

    /**
     * 统计项目指定状态的单元数量
     * 
     * @param projectId 项目ID
     * @param unitStatus 单元状态
     * @return 单元数量
     */
    int countUnitsByProjectIdAndStatus(Long projectId, Integer unitStatus);

    /**
     * 分配单元给用户（认养时调用）
     * 
     * @param projectId 项目ID
     * @param unitCount 需要分配的单元数量
     * @return 分配的单元ID列表
     */
    List<Long> allocateUnits(Long projectId, Integer unitCount);

    /**
     * 释放单元（取消认养时调用）
     * 
     * @param unitIds 单元ID列表
     * @return 释放结果
     */
    boolean releaseUnits(List<Long> unitIds);

    /**
     * 获取单元使用情况统计
     * 
     * @param projectId 项目ID
     * @return 使用情况统计
     */
    Object getUnitUsageStatistics(Long projectId);

    /**
     * 生成单元编号
     * 
     * @param projectId 项目ID
     * @param sequence 序号
     * @return 单元编号
     */
    String generateUnitNumber(Long projectId, Integer sequence);

    /**
     * 检查用户是否有权限操作单元
     * 
     * @param userId 用户ID
     * @param unitId 单元ID
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, Long unitId);

    /**
     * 开始单元种植
     * 
     * @param unitIds 单元ID列表
     * @return 操作结果
     */
    boolean startPlanting(List<Long> unitIds);

    /**
     * 单元收获完成
     * 
     * @param unitIds 单元ID列表
     * @return 操作结果
     */
    boolean completeHarvest(List<Long> unitIds);
}
