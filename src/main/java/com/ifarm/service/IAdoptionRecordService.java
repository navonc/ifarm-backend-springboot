package com.ifarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.AdoptionRecord;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户认养记录服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IAdoptionRecordService extends IService<AdoptionRecord> {

    /**
     * 根据用户ID查询认养记录列表
     * 
     * @param userId 用户ID
     * @return 认养记录列表
     */
    List<AdoptionRecord> getRecordsByUserId(Long userId);

    /**
     * 根据项目ID查询认养记录列表
     * 
     * @param projectId 项目ID
     * @return 认养记录列表
     */
    List<AdoptionRecord> getRecordsByProjectId(Long projectId);

    /**
     * 根据订单ID查询认养记录列表
     * 
     * @param orderId 订单ID
     * @return 认养记录列表
     */
    List<AdoptionRecord> getRecordsByOrderId(Long orderId);

    /**
     * 根据单元ID查询认养记录
     * 
     * @param unitId 单元ID
     * @return 认养记录
     */
    AdoptionRecord getRecordByUnitId(Long unitId);

    /**
     * 分页查询用户认养记录
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param adoptionStatus 认养状态（可选）
     * @return 分页结果
     */
    IPage<AdoptionRecord> getUserAdoptionPage(Page<AdoptionRecord> page, Long userId, Integer adoptionStatus);

    /**
     * 根据认养状态查询记录列表
     * 
     * @param adoptionStatus 认养状态
     * @return 认养记录列表
     */
    List<AdoptionRecord> getRecordsByStatus(Integer adoptionStatus);

    /**
     * 创建认养记录
     * 
     * @param orderId 订单ID
     * @param unitIds 单元ID列表
     * @return 创建结果
     */
    boolean createRecords(Long orderId, List<Long> unitIds);

    /**
     * 更新认养记录
     * 
     * @param adoptionRecord 认养记录
     * @return 更新结果
     */
    boolean updateRecord(AdoptionRecord adoptionRecord);

    /**
     * 更新认养状态
     * 
     * @param recordId 记录ID
     * @param adoptionStatus 认养状态
     * @return 更新结果
     */
    boolean updateAdoptionStatus(Long recordId, Integer adoptionStatus);

    /**
     * 批量更新认养状态
     * 
     * @param recordIds 记录ID列表
     * @param adoptionStatus 新状态
     * @return 更新结果
     */
    boolean batchUpdateAdoptionStatus(List<Long> recordIds, Integer adoptionStatus);

    /**
     * 开始种植
     * 
     * @param recordIds 记录ID列表
     * @return 操作结果
     */
    boolean startPlanting(List<Long> recordIds);

    /**
     * 开始收获
     * 
     * @param recordIds 记录ID列表
     * @return 操作结果
     */
    boolean startHarvesting(List<Long> recordIds);

    /**
     * 完成收获
     * 
     * @param recordId 记录ID
     * @param actualYield 实际产量
     * @param qualityGrade 品质等级
     * @return 操作结果
     */
    boolean completeHarvest(Long recordId, BigDecimal actualYield, String qualityGrade);

    /**
     * 批量完成收获
     * 
     * @param recordIds 记录ID列表
     * @param actualYield 实际产量
     * @param qualityGrade 品质等级
     * @return 操作结果
     */
    boolean batchCompleteHarvest(List<Long> recordIds, BigDecimal actualYield, String qualityGrade);

    /**
     * 完成认养
     * 
     * @param recordId 记录ID
     * @return 操作结果
     */
    boolean completeAdoption(Long recordId);

    /**
     * 获取认养记录详情（包含项目、作物、农场信息）
     * 
     * @param recordId 记录ID
     * @return 认养记录详情
     */
    AdoptionRecord getRecordDetail(Long recordId);

    /**
     * 检查用户是否有权限操作记录
     * 
     * @param userId 用户ID
     * @param recordId 记录ID
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, Long recordId);

    /**
     * 统计用户认养记录数量
     * 
     * @param userId 用户ID
     * @param adoptionStatus 认养状态（可选）
     * @return 记录数量
     */
    int countUserAdoptions(Long userId, Integer adoptionStatus);

    /**
     * 统计用户认养总产量
     * 
     * @param userId 用户ID
     * @return 总产量
     */
    BigDecimal sumUserYield(Long userId);

    /**
     * 获取用户认养统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Object getUserAdoptionStatistics(Long userId);

    /**
     * 获取项目认养统计信息
     * 
     * @param projectId 项目ID
     * @return 统计信息
     */
    Object getProjectAdoptionStatistics(Long projectId);

    /**
     * 查询即将收获的认养记录
     * 
     * @param days 提前天数
     * @return 即将收获的记录列表
     */
    List<AdoptionRecord> getUpcomingHarvestRecords(Integer days);

    /**
     * 查询已完成的认养记录
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 已完成的记录列表
     */
    List<AdoptionRecord> getCompletedRecords(Long userId, Integer limit);

    /**
     * 获取认养证书信息
     * 
     * @param recordId 记录ID
     * @return 证书信息
     */
    Object getAdoptionCertificate(Long recordId);
}
