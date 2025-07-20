package com.ifarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.GrowthRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * 生长记录服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IGrowthRecordService extends IService<GrowthRecord> {

    /**
     * 根据项目ID查询生长记录列表
     * 
     * @param projectId 项目ID
     * @return 生长记录列表
     */
    List<GrowthRecord> getRecordsByProjectId(Long projectId);

    /**
     * 根据项目ID和日期范围查询生长记录
     * 
     * @param projectId 项目ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 生长记录列表
     */
    List<GrowthRecord> getRecordsByProjectIdAndDateRange(Long projectId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据记录人ID查询生长记录列表
     * 
     * @param recorderId 记录人ID
     * @return 生长记录列表
     */
    List<GrowthRecord> getRecordsByRecorderId(Long recorderId);

    /**
     * 分页查询生长记录
     * 
     * @param page 分页参数
     * @param projectId 项目ID（可选）
     * @param growthStage 生长阶段（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 分页结果
     */
    IPage<GrowthRecord> getGrowthRecordPage(Page<GrowthRecord> page, Long projectId, 
                                           String growthStage, LocalDate startDate, LocalDate endDate);

    /**
     * 根据生长阶段查询记录列表
     * 
     * @param growthStage 生长阶段
     * @return 生长记录列表
     */
    List<GrowthRecord> getRecordsByGrowthStage(String growthStage);

    /**
     * 创建生长记录
     * 
     * @param growthRecord 生长记录
     * @return 创建结果
     */
    boolean createRecord(GrowthRecord growthRecord);

    /**
     * 更新生长记录
     * 
     * @param growthRecord 生长记录
     * @return 更新结果
     */
    boolean updateRecord(GrowthRecord growthRecord);

    /**
     * 删除生长记录
     * 
     * @param recordId 记录ID
     * @return 删除结果
     */
    boolean deleteRecord(Long recordId);

    /**
     * 查询项目最新的生长记录
     * 
     * @param projectId 项目ID
     * @return 最新生长记录
     */
    GrowthRecord getLatestRecordByProjectId(Long projectId);

    /**
     * 获取生长记录详情
     * 
     * @param recordId 记录ID
     * @return 生长记录详情
     */
    GrowthRecord getRecordDetail(Long recordId);

    /**
     * 检查用户是否有权限操作记录
     * 
     * @param userId 用户ID
     * @param recordId 记录ID
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, Long recordId);

    /**
     * 检查项目当天是否已有记录
     * 
     * @param projectId 项目ID
     * @param recordDate 记录日期
     * @param excludeId 排除的记录ID（用于更新时检查）
     * @return 是否已有记录
     */
    boolean existsByProjectIdAndDate(Long projectId, LocalDate recordDate, Long excludeId);

    /**
     * 统计项目的生长记录数量
     * 
     * @param projectId 项目ID
     * @return 记录数量
     */
    int countRecordsByProjectId(Long projectId);

    /**
     * 获取项目生长时间线
     * 
     * @param projectId 项目ID
     * @return 生长时间线
     */
    List<GrowthRecord> getGrowthTimeline(Long projectId);

    /**
     * 获取生长统计信息
     * 
     * @param projectId 项目ID
     * @return 统计信息
     */
    Object getGrowthStatistics(Long projectId);

    /**
     * 获取生长阶段列表
     * 
     * @return 生长阶段列表
     */
    List<String> getGrowthStages();

    /**
     * 批量创建生长记录模板
     * 
     * @param projectId 项目ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param interval 间隔天数
     * @return 创建结果
     */
    boolean batchCreateRecordTemplate(Long projectId, LocalDate startDate, LocalDate endDate, Integer interval);

    /**
     * 获取用户可查看的生长记录
     * 
     * @param userId 用户ID
     * @param projectId 项目ID
     * @return 生长记录列表
     */
    List<GrowthRecord> getUserViewableRecords(Long userId, Long projectId);

    /**
     * 获取生长记录摘要（用于用户查看）
     * 
     * @param projectId 项目ID
     * @param limit 限制数量
     * @return 记录摘要列表
     */
    List<GrowthRecord> getRecordSummary(Long projectId, Integer limit);

    /**
     * 导出生长记录
     * 
     * @param projectId 项目ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 导出数据
     */
    Object exportGrowthRecords(Long projectId, LocalDate startDate, LocalDate endDate);
}
