package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.GrowthRecord;
import com.ifarm.mapper.GrowthRecordMapper;
import com.ifarm.service.IGrowthRecordService;
import com.ifarm.service.IAdoptionRecordService;
import com.ifarm.service.IProjectUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final IAdoptionRecordService adoptionRecordService;
    private final IProjectUnitService projectUnitService;

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
    public List<GrowthRecord> getRecordsByUnitId(Long unitId) {
        if (unitId == null) {
            throw new BusinessException("单元ID不能为空");
        }
        
        log.debug("根据单元ID查询生长记录列表: {}", unitId);
        try {
            List<GrowthRecord> records = growthRecordMapper.selectByUnitId(unitId);
            log.debug("查询到{}条生长记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据单元ID查询生长记录列表失败，单元ID: {}", unitId, e);
            throw new BusinessException("查询生长记录列表失败");
        }
    }

    @Override
    public List<GrowthRecord> getRecordsByDateRange(Long projectId, LocalDate startDate, LocalDate endDate) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("根据日期范围查询生长记录: projectId={}, startDate={}, endDate={}", projectId, startDate, endDate);
        try {
            List<GrowthRecord> records = growthRecordMapper.selectByDateRange(projectId, startDate, endDate);
            log.debug("查询到{}条生长记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据日期范围查询生长记录失败", e);
            throw new BusinessException("查询生长记录失败");
        }
    }

    @Override
    public List<GrowthRecord> getRecordsByGrowthStage(Long projectId, String growthStage) {
        if (projectId == null || !StringUtils.hasText(growthStage)) {
            throw new BusinessException("参数不能为空");
        }
        
        log.debug("根据生长阶段查询记录: projectId={}, growthStage={}", projectId, growthStage);
        try {
            List<GrowthRecord> records = growthRecordMapper.selectByGrowthStage(projectId, growthStage);
            log.debug("查询到{}条{}阶段的生长记录", records.size(), growthStage);
            return records;
        } catch (Exception e) {
            log.error("根据生长阶段查询记录失败", e);
            throw new BusinessException("查询生长记录失败");
        }
    }

    @Override
    public IPage<GrowthRecord> getGrowthRecordPage(Page<GrowthRecord> page, Long projectId, 
                                                  Long unitId, String growthStage) {
        log.debug("分页查询生长记录: projectId={}, unitId={}, growthStage={}", projectId, unitId, growthStage);
        try {
            IPage<GrowthRecord> result = growthRecordMapper.selectGrowthRecordPage(page, projectId, unitId, growthStage);
            log.debug("查询到{}条生长记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("分页查询生长记录失败", e);
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
        
        log.info("创建生长记录: 项目ID={}, 单元ID={}, 记录日期={}", 
                growthRecord.getProjectId(), growthRecord.getUnitId(), growthRecord.getRecordDate());
        try {
            // 验证项目和单元是否存在
            validateRelatedData(growthRecord);
            
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
    public boolean batchCreateRecords(List<GrowthRecord> growthRecords) {
        if (growthRecords == null || growthRecords.isEmpty()) {
            throw new BusinessException("生长记录列表不能为空");
        }
        
        log.info("批量创建生长记录，数量: {}", growthRecords.size());
        try {
            // 验证每条记录
            for (GrowthRecord record : growthRecords) {
                validateRecordFields(record);
                if (record.getRecordDate() == null) {
                    record.setRecordDate(LocalDate.now());
                }
            }
            
            boolean result = saveBatch(growthRecords);
            if (result) {
                log.info("批量创建生长记录成功，创建{}条记录", growthRecords.size());
            } else {
                log.error("批量创建生长记录失败");
                throw new BusinessException("批量创建生长记录失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量创建生长记录失败", e);
            throw new BusinessException("批量创建生长记录失败");
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
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteRecords(List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            throw new BusinessException("记录ID列表不能为空");
        }
        
        log.info("批量删除生长记录: IDs={}", recordIds);
        try {
            boolean result = removeByIds(recordIds);
            if (result) {
                log.info("批量删除生长记录成功，删除{}条记录", recordIds.size());
            } else {
                log.error("批量删除生长记录失败");
                throw new BusinessException("批量删除生长记录失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除生长记录失败", e);
            throw new BusinessException("批量删除生长记录失败");
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
    public GrowthRecord getLatestRecord(Long unitId) {
        if (unitId == null) {
            throw new BusinessException("单元ID不能为空");
        }
        
        log.debug("获取单元最新生长记录: unitId={}", unitId);
        try {
            GrowthRecord record = growthRecordMapper.selectLatestByUnitId(unitId);
            return record;
        } catch (Exception e) {
            log.error("获取单元最新生长记录失败", e);
            throw new BusinessException("获取最新生长记录失败");
        }
    }

    @Override
    public List<GrowthRecord> getGrowthTimeline(Long unitId) {
        if (unitId == null) {
            throw new BusinessException("单元ID不能为空");
        }
        
        log.debug("获取单元生长时间线: unitId={}", unitId);
        try {
            LambdaQueryWrapper<GrowthRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GrowthRecord::getUnitId, unitId);
            wrapper.orderByAsc(GrowthRecord::getRecordDate);
            
            List<GrowthRecord> records = list(wrapper);
            log.debug("获取到{}条生长记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("获取单元生长时间线失败", e);
            throw new BusinessException("获取生长时间线失败");
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
            
            // 检查用户是否为该记录所属项目单元的农场主
            return projectUnitService.hasPermission(userId, record.getUnitId());
        } catch (Exception e) {
            log.error("检查用户生长记录操作权限失败", e);
            return false;
        }
    }

    @Override
    public int countRecordsByProject(Long projectId) {
        if (projectId == null) {
            return 0;
        }
        
        return growthRecordMapper.countByProjectId(projectId);
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
            statistics.put("totalRecords", countRecordsByProject(projectId));
            
            // 按生长阶段统计
            Map<String, Integer> stageStats = new HashMap<>();
            stageStats.put("seedling", countRecordsByStage(projectId, "幼苗期"));
            stageStats.put("growing", countRecordsByStage(projectId, "生长期"));
            stageStats.put("flowering", countRecordsByStage(projectId, "开花期"));
            stageStats.put("fruiting", countRecordsByStage(projectId, "结果期"));
            stageStats.put("mature", countRecordsByStage(projectId, "成熟期"));
            statistics.put("stageStatistics", stageStats);
            
            // 最近记录时间
            GrowthRecord latestRecord = getLatestRecordByProject(projectId);
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
    public List<GrowthRecord> getRecentRecords(Long projectId, Integer days) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        if (days == null || days <= 0) {
            days = 7; // 默认7天
        }
        
        log.debug("获取项目最近生长记录: projectId={}, days={}", projectId, days);
        try {
            LocalDate startDate = LocalDate.now().minusDays(days);
            LocalDate endDate = LocalDate.now();
            
            return getRecordsByDateRange(projectId, startDate, endDate);
        } catch (Exception e) {
            log.error("获取项目最近生长记录失败", e);
            throw new BusinessException("获取最近生长记录失败");
        }
    }

    /**
     * 验证生长记录必填字段
     */
    private void validateRecordFields(GrowthRecord record) {
        if (record.getProjectId() == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        if (record.getUnitId() == null) {
            throw new BusinessException("单元ID不能为空");
        }
        
        if (!StringUtils.hasText(record.getGrowthStage())) {
            throw new BusinessException("生长阶段不能为空");
        }
    }

    /**
     * 验证关联数据是否存在
     */
    private void validateRelatedData(GrowthRecord record) {
        // 验证项目单元是否存在
        if (projectUnitService.getById(record.getUnitId()) == null) {
            throw new BusinessException("项目单元不存在");
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

    /**
     * 获取项目最新记录
     */
    private GrowthRecord getLatestRecordByProject(Long projectId) {
        LambdaQueryWrapper<GrowthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GrowthRecord::getProjectId, projectId);
        wrapper.orderByDesc(GrowthRecord::getRecordDate);
        wrapper.last("LIMIT 1");
        
        List<GrowthRecord> records = list(wrapper);
        return records.isEmpty() ? null : records.get(0);
    }
}
