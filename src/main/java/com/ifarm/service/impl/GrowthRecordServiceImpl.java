package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.GrowthRecord;
import com.ifarm.mapper.GrowthRecordMapper;
import com.ifarm.service.IGrowthRecordService;
import com.ifarm.service.IAdoptionProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 生长记录服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GrowthRecordServiceImpl extends ServiceImpl<GrowthRecordMapper, GrowthRecord> implements IGrowthRecordService {

    private final GrowthRecordMapper growthRecordMapper;
    private final IAdoptionProjectService adoptionProjectService;

    @Override
    public List<GrowthRecord> getRecordsByProjectId(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("根据项目ID查询生长记录列表: {}", projectId);
        try {
            List<GrowthRecord> records = growthRecordMapper.selectByProjectId(projectId);
            log.debug("查询到{}条生长记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据项目ID查询生长记录列表失败，项目ID: {}", projectId, e);
            throw new BusinessException("查询生长记录列表失败");
        }
    }

    @Override
    public List<GrowthRecord> getRecordsByProjectIdAndDateRange(Long projectId, LocalDate startDate, LocalDate endDate) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("根据项目ID和日期范围查询生长记录: projectId={}, startDate={}, endDate={}", projectId, startDate, endDate);
        try {
            List<GrowthRecord> records = growthRecordMapper.selectByProjectIdAndDateRange(projectId, startDate, endDate);
            log.debug("查询到{}条生长记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据项目ID和日期范围查询生长记录失败", e);
            throw new BusinessException("查询生长记录失败");
        }
    }

    @Override
    public List<GrowthRecord> getRecordsByRecorderId(Long recorderId) {
        if (recorderId == null) {
            throw new BusinessException("记录人ID不能为空");
        }
        
        log.debug("根据记录人ID查询生长记录列表: {}", recorderId);
        try {
            List<GrowthRecord> records = growthRecordMapper.selectByRecorderId(recorderId);
            log.debug("查询到{}条生长记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据记录人ID查询生长记录列表失败，记录人ID: {}", recorderId, e);
            throw new BusinessException("查询生长记录列表失败");
        }
    }

    @Override
    public IPage<GrowthRecord> getGrowthRecordPage(Page<GrowthRecord> page, Long projectId, 
                                                  String growthStage, LocalDate startDate, LocalDate endDate) {
        log.debug("分页查询生长记录: projectId={}, growthStage={}, startDate={}, endDate={}", 
                projectId, growthStage, startDate, endDate);
        try {
            IPage<GrowthRecord> result = growthRecordMapper.selectGrowthRecordPage(page, projectId, growthStage, startDate, endDate);
            log.debug("查询到{}条生长记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("分页查询生长记录失败", e);
            throw new BusinessException("查询生长记录失败");
        }
    }

    @Override
    public List<GrowthRecord> getRecordsByGrowthStage(String growthStage) {
        if (!StringUtils.hasText(growthStage)) {
            throw new BusinessException("生长阶段不能为空");
        }
        
        log.debug("根据生长阶段查询记录列表: {}", growthStage);
        try {
            List<GrowthRecord> records = growthRecordMapper.selectByGrowthStage(growthStage);
            log.debug("查询到{}条{}阶段的生长记录", records.size(), growthStage);
            return records;
        } catch (Exception e) {
            log.error("根据生长阶段查询记录列表失败", e);
            throw new BusinessException("查询生长记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRecord(GrowthRecord growthRecord) {
        if (growthRecord == null) {
            throw new BusinessException("生长记录信息不能为空");
        }
        
        // 验证必填字段
        validateRecordFields(growthRecord);
        
        log.info("创建生长记录: 项目ID={}, 记录日期={}", growthRecord.getProjectId(), growthRecord.getRecordDate());
        try {
            // 验证项目是否存在
            if (adoptionProjectService.getById(growthRecord.getProjectId()) == null) {
                throw new BusinessException("项目不存在");
            }
            
            // 设置默认值
            if (growthRecord.getRecordDate() == null) {
                growthRecord.setRecordDate(LocalDate.now());
            }
            
            boolean result = save(growthRecord);
            if (result) {
                log.info("生长记录创建成功，ID: {}", growthRecord.getId());
            } else {
                log.error("生长记录创建失败");
                throw new BusinessException("生长记录创建失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建生长记录失败", e);
            throw new BusinessException("创建生长记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRecord(GrowthRecord growthRecord) {
        if (growthRecord == null || growthRecord.getId() == null) {
            throw new BusinessException("生长记录信息不完整");
        }
        
        log.info("更新生长记录: ID={}", growthRecord.getId());
        try {
            // 验证记录是否存在
            GrowthRecord existingRecord = getById(growthRecord.getId());
            if (existingRecord == null) {
                throw new BusinessException("生长记录不存在");
            }
            
            boolean result = updateById(growthRecord);
            if (result) {
                log.info("生长记录更新成功");
            } else {
                log.error("生长记录更新失败");
                throw new BusinessException("生长记录更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新生长记录失败", e);
            throw new BusinessException("更新生长记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRecord(Long recordId) {
        if (recordId == null) {
            throw new BusinessException("记录ID不能为空");
        }
        
        log.info("删除生长记录: ID={}", recordId);
        try {
            // 验证记录是否存在
            GrowthRecord record = getById(recordId);
            if (record == null) {
                throw new BusinessException("生长记录不存在");
            }
            
            boolean result = removeById(recordId);
            if (result) {
                log.info("生长记录删除成功");
            } else {
                log.error("生长记录删除失败");
                throw new BusinessException("生长记录删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除生长记录失败", e);
            throw new BusinessException("删除生长记录失败");
        }
    }

    @Override
    public GrowthRecord getLatestRecordByProjectId(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("查询项目最新的生长记录: projectId={}", projectId);
        try {
            return growthRecordMapper.selectLatestByProjectId(projectId);
        } catch (Exception e) {
            log.error("查询项目最新的生长记录失败", e);
            throw new BusinessException("查询最新生长记录失败");
        }
    }

    @Override
    public GrowthRecord getRecordDetail(Long recordId) {
        if (recordId == null) {
            throw new BusinessException("记录ID不能为空");
        }
        
        log.debug("获取生长记录详情: ID={}", recordId);
        try {
            GrowthRecord record = getById(recordId);
            if (record == null) {
                throw new BusinessException("生长记录不存在");
            }
            return record;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取生长记录详情失败", e);
            throw new BusinessException("获取生长记录详情失败");
        }
    }

    @Override
    public boolean hasPermission(Long userId, Long recordId) {
        if (userId == null || recordId == null) {
            return false;
        }
        
        log.debug("检查用户生长记录操作权限: userId={}, recordId={}", userId, recordId);
        try {
            // 获取记录信息
            GrowthRecord record = getById(recordId);
            if (record == null) {
                return false;
            }
            
            // 检查用户是否为记录人或项目农场主
            if (record.getRecorderId() != null && record.getRecorderId().equals(userId)) {
                return true;
            }
            
            // 检查用户是否为该记录所属项目的农场主
            return adoptionProjectService.hasPermission(userId, record.getProjectId());
        } catch (Exception e) {
            log.error("检查用户生长记录操作权限失败", e);
            return false;
        }
    }

    @Override
    public boolean existsByProjectIdAndDate(Long projectId, LocalDate recordDate, Long excludeId) {
        if (projectId == null || recordDate == null) {
            return false;
        }
        
        try {
            GrowthRecord existingRecord = growthRecordMapper.selectByProjectIdAndDate(projectId, recordDate);
            if (existingRecord == null) {
                return false;
            }
            
            // 如果有排除ID，检查是否为同一条记录
            return !existingRecord.getId().equals(excludeId);
        } catch (Exception e) {
            log.error("检查项目日期记录是否存在失败", e);
            return false;
        }
    }

    @Override
    public int countRecordsByProjectId(Long projectId) {
        if (projectId == null) {
            return 0;
        }
        
        return growthRecordMapper.countByProjectId(projectId);
    }

    @Override
    public List<GrowthRecord> getGrowthTimeline(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("获取项目生长时间线: projectId={}", projectId);
        try {
            LambdaQueryWrapper<GrowthRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GrowthRecord::getProjectId, projectId);
            wrapper.orderByAsc(GrowthRecord::getRecordDate);
            
            List<GrowthRecord> records = list(wrapper);
            log.debug("获取到{}条生长记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("获取项目生长时间线失败", e);
            throw new BusinessException("获取生长时间线失败");
        }
    }

    @Override
    public Object getGrowthStatistics(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("获取项目生长统计信息: projectId={}", projectId);
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总记录数
            statistics.put("totalRecords", countRecordsByProjectId(projectId));
            
            // 按生长阶段统计
            Map<String, Integer> stageStats = new HashMap<>();
            List<String> stages = getGrowthStages();
            for (String stage : stages) {
                stageStats.put(stage, countRecordsByStage(projectId, stage));
            }
            statistics.put("stageStatistics", stageStats);
            
            // 最近记录时间
            GrowthRecord latestRecord = getLatestRecordByProjectId(projectId);
            if (latestRecord != null) {
                statistics.put("latestRecordDate", latestRecord.getRecordDate());
                statistics.put("latestGrowthStage", latestRecord.getGrowthStage());
            }
            
            return statistics;
        } catch (Exception e) {
            log.error("获取项目生长统计信息失败", e);
            throw new BusinessException("获取生长统计信息失败");
        }
    }

    @Override
    public List<String> getGrowthStages() {
        log.debug("获取生长阶段列表");
        // 返回常用的生长阶段列表
        return Arrays.asList(
            "播种期", "发芽期", "幼苗期", "生长期", "开花期", "结果期", "成熟期"
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreateRecordTemplate(Long projectId, LocalDate startDate, LocalDate endDate, Integer interval) {
        if (projectId == null || startDate == null || endDate == null || interval == null || interval <= 0) {
            throw new BusinessException("参数无效");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }
        
        log.info("批量创建生长记录模板: projectId={}, startDate={}, endDate={}, interval={}", 
                projectId, startDate, endDate, interval);
        try {
            // 验证项目是否存在
            if (adoptionProjectService.getById(projectId) == null) {
                throw new BusinessException("项目不存在");
            }
            
            List<GrowthRecord> templates = new ArrayList<>();
            LocalDate currentDate = startDate;
            
            while (!currentDate.isAfter(endDate)) {
                // 检查当天是否已有记录
                if (!existsByProjectIdAndDate(projectId, currentDate, null)) {
                    GrowthRecord template = new GrowthRecord();
                    template.setProjectId(projectId);
                    template.setRecordDate(currentDate);
                    template.setGrowthStage("待记录");
                    template.setGrowthStatus("待记录");
                    template.setNotes("模板记录，请及时更新");
                    
                    templates.add(template);
                }
                
                currentDate = currentDate.plusDays(interval);
            }
            
            if (templates.isEmpty()) {
                log.info("指定日期范围内已存在记录，无需创建模板");
                return true;
            }
            
            boolean result = saveBatch(templates);
            if (result) {
                log.info("批量创建生长记录模板成功，创建{}条记录", templates.size());
            } else {
                log.error("批量创建生长记录模板失败");
                throw new BusinessException("批量创建生长记录模板失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量创建生长记录模板失败", e);
            throw new BusinessException("批量创建生长记录模板失败");
        }
    }

    @Override
    public List<GrowthRecord> getUserViewableRecords(Long userId, Long projectId) {
        if (userId == null || projectId == null) {
            throw new BusinessException("参数不能为空");
        }
        
        log.debug("获取用户可查看的生长记录: userId={}, projectId={}", userId, projectId);
        try {
            // 检查用户是否有权限查看该项目的记录
            if (!adoptionProjectService.hasPermission(userId, projectId)) {
                // 如果不是农场主，只能查看自己记录的
                return getRecordsByRecorderId(userId);
            } else {
                // 如果是农场主，可以查看项目所有记录
                return getRecordsByProjectId(projectId);
            }
        } catch (Exception e) {
            log.error("获取用户可查看的生长记录失败", e);
            throw new BusinessException("获取用户可查看的生长记录失败");
        }
    }

    /**
     * 验证生长记录必填字段
     */
    private void validateRecordFields(GrowthRecord record) {
        if (record.getProjectId() == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        if (record.getRecordDate() == null) {
            throw new BusinessException("记录日期不能为空");
        }
    }

    @Override
    public List<GrowthRecord> getRecordSummary(Long projectId, Integer limit) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }

        Integer queryLimit = limit != null ? limit : 10; // 默认10条
        log.debug("获取项目生长记录摘要: projectId={}, limit={}", projectId, queryLimit);

        try {
            LambdaQueryWrapper<GrowthRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GrowthRecord::getProjectId, projectId);
            wrapper.orderByDesc(GrowthRecord::getRecordDate);
            wrapper.last("LIMIT " + queryLimit);

            List<GrowthRecord> records = list(wrapper);
            log.debug("获取到{}条生长记录摘要", records.size());
            return records;
        } catch (Exception e) {
            log.error("获取项目生长记录摘要失败", e);
            throw new BusinessException("获取生长记录摘要失败");
        }
    }

    @Override
    public Object exportGrowthRecords(Long projectId, LocalDate startDate, LocalDate endDate) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }

        log.debug("导出生长记录: projectId={}, startDate={}, endDate={}", projectId, startDate, endDate);
        try {
            List<GrowthRecord> records = getRecordsByProjectIdAndDateRange(projectId, startDate, endDate);

            Map<String, Object> exportData = new HashMap<>();
            exportData.put("projectId", projectId);
            exportData.put("startDate", startDate);
            exportData.put("endDate", endDate);
            exportData.put("totalRecords", records.size());
            exportData.put("records", records);
            exportData.put("exportTime", LocalDateTime.now());

            return exportData;
        } catch (Exception e) {
            log.error("导出生长记录失败", e);
            throw new BusinessException("导出生长记录失败");
        }
    }

    /**
     * 统计指定阶段的记录数量
     */
    private int countRecordsByStage(Long projectId, String growthStage) {
        LambdaQueryWrapper<GrowthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GrowthRecord::getProjectId, projectId);
        wrapper.eq(GrowthRecord::getGrowthStage, growthStage);
        return Math.toIntExact(count(wrapper));
    }
}
