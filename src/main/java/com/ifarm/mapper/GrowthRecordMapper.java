package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.entity.GrowthRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 生长记录Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface GrowthRecordMapper extends BaseMapper<GrowthRecord> {

    /**
     * 根据项目ID查询生长记录列表
     * 
     * @param projectId 项目ID
     * @return 生长记录列表
     */
    List<GrowthRecord> selectByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据项目ID和日期范围查询生长记录
     * 
     * @param projectId 项目ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 生长记录列表
     */
    List<GrowthRecord> selectByProjectIdAndDateRange(@Param("projectId") Long projectId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 根据记录人ID查询生长记录列表
     * 
     * @param recorderId 记录人ID
     * @return 生长记录列表
     */
    List<GrowthRecord> selectByRecorderId(@Param("recorderId") Long recorderId);

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
    IPage<GrowthRecord> selectGrowthRecordPage(Page<GrowthRecord> page,
                                               @Param("projectId") Long projectId,
                                               @Param("growthStage") String growthStage,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 根据生长阶段查询记录列表
     * 
     * @param growthStage 生长阶段
     * @return 生长记录列表
     */
    List<GrowthRecord> selectByGrowthStage(@Param("growthStage") String growthStage);

    /**
     * 查询项目最新的生长记录
     * 
     * @param projectId 项目ID
     * @return 最新生长记录
     */
    GrowthRecord selectLatestByProjectId(@Param("projectId") Long projectId);

    /**
     * 统计项目的生长记录数量
     * 
     * @param projectId 项目ID
     * @return 记录数量
     */
    int countByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据项目ID和记录日期查询记录
     * 
     * @param projectId 项目ID
     * @param recordDate 记录日期
     * @return 生长记录
     */
    GrowthRecord selectByProjectIdAndDate(@Param("projectId") Long projectId, 
                                          @Param("recordDate") LocalDate recordDate);
}
