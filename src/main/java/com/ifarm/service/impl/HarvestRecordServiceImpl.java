package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.HarvestRecord;
import com.ifarm.mapper.HarvestRecordMapper;
import com.ifarm.service.IHarvestRecordService;
import com.ifarm.service.IAdoptionProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * 收获记录服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HarvestRecordServiceImpl extends ServiceImpl<HarvestRecordMapper, HarvestRecord> implements IHarvestRecordService {

    private final HarvestRecordMapper harvestRecordMapper;
    private final IAdoptionProjectService adoptionProjectService;

    @Override
    public List<HarvestRecord> getRecordsByProjectId(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("根据项目ID查询收获记录列表: {}", projectId);
        try {
            List<HarvestRecord> records = harvestRecordMapper.selectByProjectId(projectId);
            log.debug("查询到{}条收获记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据项目ID查询收获记录列表失败，项目ID: {}", projectId, e);
            throw new BusinessException("查询收获记录列表失败");
        }
    }

    @Override
    public HarvestRecord getRecordByUnitId(Long unitId) {
        if (unitId == null) {
            throw new BusinessException("单元ID不能为空");
        }
        
        log.debug("根据单元ID查询收获记录: {}", unitId);
        try {
            return harvestRecordMapper.selectByUnitId(unitId);
        } catch (Exception e) {
            log.error("根据单元ID查询收获记录失败，单元ID: {}", unitId, e);
            throw new BusinessException("查询收获记录失败");
        }
    }

    @Override
    public List<HarvestRecord> getRecordsByHarvesterId(Long harvesterId) {
        if (harvesterId == null) {
            throw new BusinessException("收获人ID不能为空");
        }
        
        log.debug("根据收获人ID查询收获记录列表: {}", harvesterId);
        try {
            List<HarvestRecord> records = harvestRecordMapper.selectByHarvesterId(harvesterId);
            log.debug("查询到{}条收获记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据收获人ID查询收获记录列表失败，收获人ID: {}", harvesterId, e);
            throw new BusinessException("查询收获记录列表失败");
        }
    }

    @Override
    public List<HarvestRecord> getRecordsByProjectIdAndDateRange(Long projectId, LocalDate startDate, LocalDate endDate) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("根据项目ID和日期范围查询收获记录: projectId={}, startDate={}, endDate={}", projectId, startDate, endDate);
        try {
            List<HarvestRecord> records = harvestRecordMapper.selectByProjectIdAndDateRange(projectId, startDate, endDate);
            log.debug("查询到{}条收获记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据项目ID和日期范围查询收获记录失败", e);
            throw new BusinessException("查询收获记录失败");
        }
    }

    @Override
    public IPage<HarvestRecord> getHarvestRecordPage(Page<HarvestRecord> page, Long projectId, 
                                                    String qualityGrade, LocalDate startDate, LocalDate endDate) {
        log.debug("分页查询收获记录: projectId={}, qualityGrade={}, startDate={}, endDate={}", 
                projectId, qualityGrade, startDate, endDate);
        try {
            IPage<HarvestRecord> result = harvestRecordMapper.selectHarvestRecordPage(page, projectId, qualityGrade, startDate, endDate);
            log.debug("查询到{}条收获记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("分页查询收获记录失败", e);
            throw new BusinessException("查询收获记录失败");
        }
    }

    @Override
    public List<HarvestRecord> getRecordsByQualityGrade(String qualityGrade) {
        if (!StringUtils.hasText(qualityGrade)) {
            throw new BusinessException("品质等级不能为空");
        }
        
        log.debug("根据品质等级查询收获记录列表: {}", qualityGrade);
        try {
            List<HarvestRecord> records = harvestRecordMapper.selectByQualityGrade(qualityGrade);
            log.debug("查询到{}条{}等级的收获记录", records.size(), qualityGrade);
            return records;
        } catch (Exception e) {
            log.error("根据品质等级查询收获记录列表失败", e);
            throw new BusinessException("查询收获记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRecord(HarvestRecord harvestRecord) {
        if (harvestRecord == null) {
            throw new BusinessException("收获记录信息不能为空");
        }
        
        // 验证必填字段
        validateRecordFields(harvestRecord);
        
        log.info("创建收获记录: 项目ID={}, 单元ID={}, 收获日期={}", 
                harvestRecord.getProjectId(), harvestRecord.getUnitId(), harvestRecord.getHarvestDate());
        try {
            // 验证项目是否存在
            if (adoptionProjectService.getById(harvestRecord.getProjectId()) == null) {
                throw new BusinessException("项目不存在");
            }
            
            // 设置默认值
            if (harvestRecord.getHarvestDate() == null) {
                harvestRecord.setHarvestDate(LocalDate.now());
            }
            
            boolean result = save(harvestRecord);
            if (result) {
                log.info("收获记录创建成功，ID: {}", harvestRecord.getId());
            } else {
                log.error("收获记录创建失败");
                throw new BusinessException("收获记录创建失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建收获记录失败", e);
            throw new BusinessException("创建收获记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRecord(HarvestRecord harvestRecord) {
        if (harvestRecord == null || harvestRecord.getId() == null) {
            throw new BusinessException("收获记录信息不完整");
        }
        
        log.info("更新收获记录: ID={}", harvestRecord.getId());
        try {
            // 验证记录是否存在
            HarvestRecord existingRecord = getById(harvestRecord.getId());
            if (existingRecord == null) {
                throw new BusinessException("收获记录不存在");
            }
            
            boolean result = updateById(harvestRecord);
            if (result) {
                log.info("收获记录更新成功");
            } else {
                log.error("收获记录更新失败");
                throw new BusinessException("收获记录更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新收获记录失败", e);
            throw new BusinessException("更新收获记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRecord(Long recordId) {
        if (recordId == null) {
            throw new BusinessException("记录ID不能为空");
        }
        
        log.info("删除收获记录: ID={}", recordId);
        try {
            // 验证记录是否存在
            HarvestRecord record = getById(recordId);
            if (record == null) {
                throw new BusinessException("收获记录不存在");
            }
            
            boolean result = removeById(recordId);
            if (result) {
                log.info("收获记录删除成功");
            } else {
                log.error("收获记录删除失败");
                throw new BusinessException("收获记录删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除收获记录失败", e);
            throw new BusinessException("删除收获记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreateRecords(Long projectId, List<Long> unitIds, BigDecimal harvestQuantity, 
                                    String qualityGrade, LocalDate harvestDate) {
        if (projectId == null || unitIds == null || unitIds.isEmpty() || 
            harvestQuantity == null || harvestQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("参数无效");
        }
        
        log.info("批量创建收获记录: projectId={}, unitCount={}, harvestQuantity={}", 
                projectId, unitIds.size(), harvestQuantity);
        try {
            // 验证项目是否存在
            if (adoptionProjectService.getById(projectId) == null) {
                throw new BusinessException("项目不存在");
            }
            
            List<HarvestRecord> records = new ArrayList<>();
            LocalDate recordDate = harvestDate != null ? harvestDate : LocalDate.now();
            
            for (Long unitId : unitIds) {
                // 检查单元是否已有收获记录
                if (!existsByUnitId(unitId)) {
                    HarvestRecord record = new HarvestRecord();
                    record.setProjectId(projectId);
                    record.setUnitId(unitId);
                    record.setHarvestDate(recordDate);
                    record.setHarvestQuantity(harvestQuantity);
                    record.setQualityGrade(qualityGrade);
                    
                    records.add(record);
                }
            }
            
            if (records.isEmpty()) {
                log.info("所有单元已有收获记录，无需创建");
                return true;
            }
            
            boolean result = saveBatch(records);
            if (result) {
                log.info("批量创建收获记录成功，创建{}条记录", records.size());
            } else {
                log.error("批量创建收获记录失败");
                throw new BusinessException("批量创建收获记录失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量创建收获记录失败", e);
            throw new BusinessException("批量创建收获记录失败");
        }
    }

    @Override
    public HarvestRecord getRecordDetail(Long recordId) {
        if (recordId == null) {
            throw new BusinessException("记录ID不能为空");
        }
        
        log.debug("获取收获记录详情: ID={}", recordId);
        try {
            HarvestRecord record = getById(recordId);
            if (record == null) {
                throw new BusinessException("收获记录不存在");
            }
            return record;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取收获记录详情失败", e);
            throw new BusinessException("获取收获记录详情失败");
        }
    }

    @Override
    public boolean hasPermission(Long userId, Long recordId) {
        if (userId == null || recordId == null) {
            return false;
        }
        
        log.debug("检查用户收获记录操作权限: userId={}, recordId={}", userId, recordId);
        try {
            // 获取记录信息
            HarvestRecord record = getById(recordId);
            if (record == null) {
                return false;
            }
            
            // 检查用户是否为收获人或项目农场主
            if (record.getHarvesterId() != null && record.getHarvesterId().equals(userId)) {
                return true;
            }
            
            // 检查用户是否为该记录所属项目的农场主
            return adoptionProjectService.hasPermission(userId, record.getProjectId());
        } catch (Exception e) {
            log.error("检查用户收获记录操作权限失败", e);
            return false;
        }
    }

    @Override
    public BigDecimal getTotalHarvestQuantityByProjectId(Long projectId) {
        if (projectId == null) {
            return BigDecimal.ZERO;
        }
        
        log.debug("统计项目总收获量: projectId={}", projectId);
        try {
            LambdaQueryWrapper<HarvestRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HarvestRecord::getProjectId, projectId);
            wrapper.isNotNull(HarvestRecord::getHarvestQuantity);
            wrapper.select(HarvestRecord::getHarvestQuantity);
            
            List<HarvestRecord> records = list(wrapper);
            BigDecimal totalQuantity = records.stream()
                    .map(HarvestRecord::getHarvestQuantity)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.debug("项目{}总收获量: {}", projectId, totalQuantity);
            return totalQuantity;
        } catch (Exception e) {
            log.error("统计项目总收获量失败", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public int countRecordsByProjectId(Long projectId) {
        if (projectId == null) {
            return 0;
        }
        
        return harvestRecordMapper.countByProjectId(projectId);
    }

    @Override
    public HarvestRecord getLatestRecordByProjectId(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("查询项目最新的收获记录: projectId={}", projectId);
        try {
            return harvestRecordMapper.selectLatestByProjectId(projectId);
        } catch (Exception e) {
            log.error("查询项目最新的收获记录失败", e);
            throw new BusinessException("查询最新收获记录失败");
        }
    }

    @Override
    public BigDecimal getAverageQualityScoreByProjectId(Long projectId) {
        if (projectId == null) {
            return BigDecimal.ZERO;
        }
        
        log.debug("计算项目平均品质评分: projectId={}", projectId);
        try {
            LambdaQueryWrapper<HarvestRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HarvestRecord::getProjectId, projectId);
            wrapper.isNotNull(HarvestRecord::getQualityScore);
            wrapper.select(HarvestRecord::getQualityScore);
            
            List<HarvestRecord> records = list(wrapper);
            if (records.isEmpty()) {
                return BigDecimal.ZERO;
            }
            
            BigDecimal totalScore = records.stream()
                    .map(HarvestRecord::getQualityScore)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal avgScore = totalScore.divide(new BigDecimal(records.size()), 2, BigDecimal.ROUND_HALF_UP);
            log.debug("项目{}平均品质评分: {}", projectId, avgScore);
            return avgScore;
        } catch (Exception e) {
            log.error("计算项目平均品质评分失败", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Object getHarvestStatistics(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }

        log.debug("获取项目收获统计信息: projectId={}", projectId);
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 基础统计
            statistics.put("totalRecords", countRecordsByProjectId(projectId));
            statistics.put("totalQuantity", getTotalHarvestQuantityByProjectId(projectId));
            statistics.put("averageQualityScore", getAverageQualityScoreByProjectId(projectId));

            // 质量等级统计
            Map<String, Integer> qualityStats = new HashMap<>();
            List<String> grades = getQualityGrades();
            for (String grade : grades) {
                qualityStats.put(grade, countRecordsByQuality(projectId, grade));
            }
            statistics.put("qualityStatistics", qualityStats);

            // 最近收获时间
            HarvestRecord latestRecord = getLatestRecordByProjectId(projectId);
            if (latestRecord != null) {
                statistics.put("latestHarvestDate", latestRecord.getHarvestDate());
            }

            return statistics;
        } catch (Exception e) {
            log.error("获取项目收获统计信息失败", e);
            throw new BusinessException("获取收获统计信息失败");
        }
    }

    @Override
    public List<String> getQualityGrades() {
        log.debug("获取质量等级列表");
        // 返回常用的质量等级列表
        return Arrays.asList("A级", "B级", "C级", "D级");
    }

    @Override
    public List<HarvestRecord> getUserHarvestRecords(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        log.debug("获取用户收获记录: userId={}", userId);
        try {
            LambdaQueryWrapper<HarvestRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HarvestRecord::getHarvesterId, userId);
            wrapper.orderByDesc(HarvestRecord::getHarvestDate);

            List<HarvestRecord> records = list(wrapper);
            log.debug("查询到{}条用户收获记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("获取用户收获记录失败", e);
            throw new BusinessException("获取用户收获记录失败");
        }
    }

    @Override
    public boolean existsByUnitId(Long unitId) {
        if (unitId == null) {
            return false;
        }

        LambdaQueryWrapper<HarvestRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HarvestRecord::getUnitId, unitId);
        return count(wrapper) > 0;
    }

    /**
     * 验证收获记录必填字段
     */
    private void validateRecordFields(HarvestRecord record) {
        if (record.getProjectId() == null) {
            throw new BusinessException("项目ID不能为空");
        }

        if (record.getHarvestQuantity() == null || record.getHarvestQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("收获数量不能为空且不能小于0");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeProjectHarvest(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }

        log.info("完成项目收获: projectId={}", projectId);
        try {
            // 验证项目是否存在
            if (adoptionProjectService.getById(projectId) == null) {
                throw new BusinessException("项目不存在");
            }

            // 计算项目总收获量
            BigDecimal totalYield = getTotalHarvestQuantityByProjectId(projectId);
            BigDecimal avgQualityScore = getAverageQualityScoreByProjectId(projectId);

            // 创建项目整体收获记录
            HarvestRecord projectRecord = new HarvestRecord();
            projectRecord.setProjectId(projectId);
            projectRecord.setHarvestDate(LocalDate.now());
            projectRecord.setHarvestQuantity(totalYield);
            projectRecord.setQualityScore(avgQualityScore);
            projectRecord.setHarvestNotes("项目整体收获完成");

            boolean result = save(projectRecord);
            if (result) {
                log.info("项目收获完成记录创建成功，ID: {}", projectRecord.getId());
            } else {
                log.error("项目收获完成记录创建失败");
                throw new BusinessException("项目收获完成失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("完成项目收获失败", e);
            throw new BusinessException("完成项目收获失败");
        }
    }

    @Override
    public Object getHarvestCalendar(Long projectId, Integer year, Integer month) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }

        Integer queryYear = year != null ? year : LocalDate.now().getYear();
        Integer queryMonth = month != null ? month : LocalDate.now().getMonthValue();

        log.debug("获取收获日历数据: projectId={}, year={}, month={}", projectId, queryYear, queryMonth);
        try {
            LocalDate startDate = LocalDate.of(queryYear, queryMonth, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);

            List<HarvestRecord> records = getRecordsByProjectIdAndDateRange(projectId, startDate, endDate);

            Map<String, Object> calendar = new HashMap<>();
            calendar.put("year", queryYear);
            calendar.put("month", queryMonth);
            calendar.put("totalRecords", records.size());

            // 按日期分组
            Map<Integer, List<HarvestRecord>> dailyRecords = new HashMap<>();
            for (HarvestRecord record : records) {
                int day = record.getHarvestDate().getDayOfMonth();
                dailyRecords.computeIfAbsent(day, k -> new ArrayList<>()).add(record);
            }
            calendar.put("dailyRecords", dailyRecords);

            return calendar;
        } catch (Exception e) {
            log.error("获取收获日历数据失败", e);
            throw new BusinessException("获取收获日历数据失败");
        }
    }

    @Override
    public Object exportHarvestRecords(Long projectId, LocalDate startDate, LocalDate endDate) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }

        log.debug("导出收获记录: projectId={}, startDate={}, endDate={}", projectId, startDate, endDate);
        try {
            List<HarvestRecord> records = getRecordsByProjectIdAndDateRange(projectId, startDate, endDate);

            Map<String, Object> exportData = new HashMap<>();
            exportData.put("projectId", projectId);
            exportData.put("startDate", startDate);
            exportData.put("endDate", endDate);
            exportData.put("totalRecords", records.size());
            exportData.put("totalQuantity", records.stream()
                    .map(HarvestRecord::getHarvestQuantity)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            exportData.put("records", records);
            exportData.put("exportTime", LocalDate.now());

            return exportData;
        } catch (Exception e) {
            log.error("导出收获记录失败", e);
            throw new BusinessException("导出收获记录失败");
        }
    }

    @Override
    public Object generateHarvestReport(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }

        log.debug("生成收获报告: projectId={}", projectId);
        try {
            Map<String, Object> report = new HashMap<>();

            // 基础统计
            report.put("projectId", projectId);
            report.put("totalRecords", countRecordsByProjectId(projectId));
            report.put("totalQuantity", getTotalHarvestQuantityByProjectId(projectId));
            report.put("averageQualityScore", getAverageQualityScoreByProjectId(projectId));

            // 质量等级分布
            Map<String, Integer> qualityDistribution = new HashMap<>();
            List<String> grades = getQualityGrades();
            for (String grade : grades) {
                qualityDistribution.put(grade, countRecordsByQuality(projectId, grade));
            }
            report.put("qualityDistribution", qualityDistribution);

            // 最新收获记录
            HarvestRecord latestRecord = getLatestRecordByProjectId(projectId);
            report.put("latestRecord", latestRecord);

            // 生成时间
            report.put("generateTime", LocalDate.now());

            return report;
        } catch (Exception e) {
            log.error("生成收获报告失败", e);
            throw new BusinessException("生成收获报告失败");
        }
    }

    /**
     * 统计指定质量等级的记录数量
     */
    private int countRecordsByQuality(Long projectId, String qualityGrade) {
        LambdaQueryWrapper<HarvestRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HarvestRecord::getProjectId, projectId);
        wrapper.eq(HarvestRecord::getQualityGrade, qualityGrade);
        return Math.toIntExact(count(wrapper));
    }
}
