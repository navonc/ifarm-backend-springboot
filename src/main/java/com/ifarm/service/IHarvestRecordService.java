package com.ifarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.HarvestRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 收获记录服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IHarvestRecordService extends IService<HarvestRecord> {

    /**
     * 根据项目ID查询收获记录列表
     * 
     * @param projectId 项目ID
     * @return 收获记录列表
     */
    List<HarvestRecord> getRecordsByProjectId(Long projectId);

    /**
     * 根据单元ID查询收获记录
     * 
     * @param unitId 单元ID
     * @return 收获记录
     */
    HarvestRecord getRecordByUnitId(Long unitId);

    /**
     * 根据收获人ID查询收获记录列表
     * 
     * @param harvesterId 收获人ID
     * @return 收获记录列表
     */
    List<HarvestRecord> getRecordsByHarvesterId(Long harvesterId);

    /**
     * 根据项目ID和日期范围查询收获记录
     * 
     * @param projectId 项目ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收获记录列表
     */
    List<HarvestRecord> getRecordsByProjectIdAndDateRange(Long projectId, LocalDate startDate, LocalDate endDate);

    /**
     * 分页查询收获记录
     * 
     * @param page 分页参数
     * @param projectId 项目ID（可选）
     * @param qualityGrade 品质等级（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 分页结果
     */
    IPage<HarvestRecord> getHarvestRecordPage(Page<HarvestRecord> page, Long projectId, 
                                             String qualityGrade, LocalDate startDate, LocalDate endDate);

    /**
     * 根据品质等级查询收获记录列表
     * 
     * @param qualityGrade 品质等级
     * @return 收获记录列表
     */
    List<HarvestRecord> getRecordsByQualityGrade(String qualityGrade);

    /**
     * 创建收获记录
     * 
     * @param harvestRecord 收获记录
     * @return 创建结果
     */
    boolean createRecord(HarvestRecord harvestRecord);

    /**
     * 更新收获记录
     * 
     * @param harvestRecord 收获记录
     * @return 更新结果
     */
    boolean updateRecord(HarvestRecord harvestRecord);

    /**
     * 删除收获记录
     * 
     * @param recordId 记录ID
     * @return 删除结果
     */
    boolean deleteRecord(Long recordId);

    /**
     * 批量创建收获记录
     * 
     * @param projectId 项目ID
     * @param unitIds 单元ID列表
     * @param harvestQuantity 收获数量
     * @param qualityGrade 品质等级
     * @param harvestDate 收获日期
     * @return 创建结果
     */
    boolean batchCreateRecords(Long projectId, List<Long> unitIds, BigDecimal harvestQuantity, 
                              String qualityGrade, LocalDate harvestDate);

    /**
     * 获取收获记录详情
     * 
     * @param recordId 记录ID
     * @return 收获记录详情
     */
    HarvestRecord getRecordDetail(Long recordId);

    /**
     * 检查用户是否有权限操作记录
     * 
     * @param userId 用户ID
     * @param recordId 记录ID
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, Long recordId);

    /**
     * 统计项目总收获量
     * 
     * @param projectId 项目ID
     * @return 总收获量
     */
    BigDecimal getTotalHarvestQuantityByProjectId(Long projectId);

    /**
     * 统计项目收获记录数量
     * 
     * @param projectId 项目ID
     * @return 记录数量
     */
    int countRecordsByProjectId(Long projectId);

    /**
     * 查询项目最新的收获记录
     * 
     * @param projectId 项目ID
     * @return 最新收获记录
     */
    HarvestRecord getLatestRecordByProjectId(Long projectId);

    /**
     * 计算项目平均品质评分
     * 
     * @param projectId 项目ID
     * @return 平均品质评分
     */
    BigDecimal getAverageQualityScoreByProjectId(Long projectId);

    /**
     * 获取收获统计信息
     * 
     * @param projectId 项目ID
     * @return 统计信息
     */
    Object getHarvestStatistics(Long projectId);

    /**
     * 获取品质等级列表
     * 
     * @return 品质等级列表
     */
    List<String> getQualityGrades();

    /**
     * 获取用户的收获记录
     * 
     * @param userId 用户ID
     * @return 收获记录列表
     */
    List<HarvestRecord> getUserHarvestRecords(Long userId);

    /**
     * 检查单元是否已有收获记录
     * 
     * @param unitId 单元ID
     * @return 是否已有记录
     */
    boolean existsByUnitId(Long unitId);

    /**
     * 完成项目收获
     * 
     * @param projectId 项目ID
     * @return 完成结果
     */
    boolean completeProjectHarvest(Long projectId);

    /**
     * 获取收获日历数据
     * 
     * @param projectId 项目ID
     * @param year 年份
     * @param month 月份
     * @return 日历数据
     */
    Object getHarvestCalendar(Long projectId, Integer year, Integer month);

    /**
     * 导出收获记录
     * 
     * @param projectId 项目ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 导出数据
     */
    Object exportHarvestRecords(Long projectId, LocalDate startDate, LocalDate endDate);

    /**
     * 生成收获报告
     * 
     * @param projectId 项目ID
     * @return 收获报告
     */
    Object generateHarvestReport(Long projectId);
}
