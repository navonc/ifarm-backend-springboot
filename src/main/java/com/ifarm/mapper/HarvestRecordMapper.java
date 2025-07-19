package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.entity.HarvestRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 收获记录Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface HarvestRecordMapper extends BaseMapper<HarvestRecord> {

    /**
     * 根据项目ID查询收获记录列表
     * 
     * @param projectId 项目ID
     * @return 收获记录列表
     */
    List<HarvestRecord> selectByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据单元ID查询收获记录
     * 
     * @param unitId 单元ID
     * @return 收获记录
     */
    HarvestRecord selectByUnitId(@Param("unitId") Long unitId);

    /**
     * 根据收获人ID查询收获记录列表
     * 
     * @param harvesterId 收获人ID
     * @return 收获记录列表
     */
    List<HarvestRecord> selectByHarvesterId(@Param("harvesterId") Long harvesterId);

    /**
     * 根据项目ID和日期范围查询收获记录
     * 
     * @param projectId 项目ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收获记录列表
     */
    List<HarvestRecord> selectByProjectIdAndDateRange(@Param("projectId") Long projectId,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

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
    IPage<HarvestRecord> selectHarvestRecordPage(Page<HarvestRecord> page,
                                                 @Param("projectId") Long projectId,
                                                 @Param("qualityGrade") String qualityGrade,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    /**
     * 根据品质等级查询收获记录列表
     * 
     * @param qualityGrade 品质等级
     * @return 收获记录列表
     */
    List<HarvestRecord> selectByQualityGrade(@Param("qualityGrade") String qualityGrade);

    /**
     * 统计项目总收获量
     * 
     * @param projectId 项目ID
     * @return 总收获量
     */
    BigDecimal sumHarvestQuantityByProjectId(@Param("projectId") Long projectId);

    /**
     * 统计项目收获记录数量
     * 
     * @param projectId 项目ID
     * @return 记录数量
     */
    int countByProjectId(@Param("projectId") Long projectId);

    /**
     * 查询项目最新的收获记录
     * 
     * @param projectId 项目ID
     * @return 最新收获记录
     */
    HarvestRecord selectLatestByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据项目ID计算平均品质评分
     * 
     * @param projectId 项目ID
     * @return 平均品质评分
     */
    BigDecimal avgQualityScoreByProjectId(@Param("projectId") Long projectId);
}
