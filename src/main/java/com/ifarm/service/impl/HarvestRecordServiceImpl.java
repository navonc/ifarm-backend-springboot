package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.HarvestRecord;
import com.ifarm.mapper.HarvestRecordMapper;
import com.ifarm.service.IHarvestRecordService;
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
    private final IAdoptionRecordService adoptionRecordService;
    private final IProjectUnitService projectUnitService;

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
    public List<HarvestRecord> getRecordsByUnitId(Long unitId) {
        if (unitId == null) {
            throw new BusinessException("单元ID不能为空");
        }
        
        log.debug("根据单元ID查询收获记录列表: {}", unitId);
        try {
            List<HarvestRecord> records = harvestRecordMapper.selectByUnitId(unitId);
            log.debug("查询到{}条收获记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据单元ID查询收获记录列表失败，单元ID: {}", unitId, e);
            throw new BusinessException("查询收获记录列表失败");
        }
    }

    @Override
    public List<HarvestRecord> getRecordsByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        log.debug("根据用户ID查询收获记录列表: {}", userId);
        try {
            List<HarvestRecord> records = harvestRecordMapper.selectByUserId(userId);
            log.debug("查询到{}条收获记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据用户ID查询收获记录列表失败，用户ID: {}", userId, e);
            throw new BusinessException("查询收获记录列表失败");
        }
    }

    @Override
    public List<HarvestRecord> getRecordsByDateRange(Long projectId, LocalDate startDate, LocalDate endDate) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("根据日期范围查询收获记录: projectId={}, startDate={}, endDate={}", projectId, startDate, endDate);
        try {
            List<HarvestRecord> records = harvestRecordMapper.selectByDateRange(projectId, startDate, endDate);
            log.debug("查询到{}条收获记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据日期范围查询收获记录失败", e);
            throw new BusinessException("查询收获记录失败");
        }
    }

    @Override
    public IPage<HarvestRecord> getHarvestRecordPage(Page<HarvestRecord> page, Long projectId, 
                                                    Long unitId, Long userId) {
        log.debug("分页查询收获记录: projectId={}, unitId={}, userId={}", projectId, unitId, userId);
        try {
            IPage<HarvestRecord> result = harvestRecordMapper.selectHarvestRecordPage(page, projectId, unitId, userId);
            log.debug("查询到{}条收获记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("分页查询收获记录失败", e);
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
        
        log.info("创建收获记录: 项目ID={}, 单元ID={}, 用户ID={}, 收获日期={}", 
                harvestRecord.getProjectId(), harvestRecord.getUnitId(), 
                harvestRecord.getUserId(), harvestRecord.getHarvestDate());
        try {
            // 验证关联数据是否存在
            validateRelatedData(harvestRecord);
            
            // 设置默认值
            if (harvestRecord.getHarvestDate() == null) {
                harvestRecord.setHarvestDate(LocalDate.now());
            }
            
            boolean result = save(harvestRecord);
            if (result) {
                // 更新认养记录的收获信息
                updateAdoptionRecordHarvest(harvestRecord);
                
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
    public boolean batchCreateRecords(List<HarvestRecord> harvestRecords) {
        if (harvestRecords == null || harvestRecords.isEmpty()) {
            throw new BusinessException("收获记录列表不能为空");
        }
        
        log.info("批量创建收获记录，数量: {}", harvestRecords.size());
        try {
            // 验证每条记录
            for (HarvestRecord record : harvestRecords) {
                validateRecordFields(record);
                if (record.getHarvestDate() == null) {
                    record.setHarvestDate(LocalDate.now());
                }
            }
            
            boolean result = saveBatch(harvestRecords);
            if (result) {
                // 批量更新认养记录
                for (HarvestRecord record : harvestRecords) {
                    updateAdoptionRecordHarvest(record);
                }
                
                log.info("批量创建收获记录成功，创建{}条记录", harvestRecords.size());
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
                // 如果更新了产量或质量等级，同步更新认养记录
                if (harvestRecord.getActualYield() != null || harvestRecord.getQualityGrade() != null) {
                    updateAdoptionRecordHarvest(harvestRecord);
                }
                
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
    public boolean batchDeleteRecords(List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            throw new BusinessException("记录ID列表不能为空");
        }
        
        log.info("批量删除收获记录: IDs={}", recordIds);
        try {
            boolean result = removeByIds(recordIds);
            if (result) {
                log.info("批量删除收获记录成功，删除{}条记录", recordIds.size());
            } else {
                log.error("批量删除收获记录失败");
                throw new BusinessException("批量删除收获记录失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除收获记录失败", e);
            throw new BusinessException("批量删除收获记录失败");
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
    public HarvestRecord getLatestRecord(Long unitId) {
        if (unitId == null) {
            throw new BusinessException("单元ID不能为空");
        }
        
        log.debug("获取单元最新收获记录: unitId={}", unitId);
        try {
            HarvestRecord record = harvestRecordMapper.selectLatestByUnitId(unitId);
            return record;
        } catch (Exception e) {
            log.error("获取单元最新收获记录失败", e);
            throw new BusinessException("获取最新收获记录失败");
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
            
            // 检查用户是否为该记录的用户或农场主
            if (record.getUserId().equals(userId)) {
                return true;
            }
            
            // 检查用户是否为该记录所属项目单元的农场主
            return projectUnitService.hasPermission(userId, record.getUnitId());
        } catch (Exception e) {
            log.error("检查用户收获记录操作权限失败", e);
            return false;
        }
    }

    @Override
    public int countRecordsByProject(Long projectId) {
        if (projectId == null) {
            return 0;
        }
        
        return harvestRecordMapper.countByProjectId(projectId);
    }

    @Override
    public int countRecordsByUser(Long userId) {
        if (userId == null) {
            return 0;
        }
        
        return harvestRecordMapper.countByUserId(userId);
    }

    @Override
    public BigDecimal sumYieldByProject(Long projectId) {
        if (projectId == null) {
            return BigDecimal.ZERO;
        }
        
        log.debug("统计项目总产量: projectId={}", projectId);
        try {
            LambdaQueryWrapper<HarvestRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HarvestRecord::getProjectId, projectId);
            wrapper.isNotNull(HarvestRecord::getActualYield);
            wrapper.select(HarvestRecord::getActualYield);
            
            List<HarvestRecord> records = list(wrapper);
            BigDecimal totalYield = records.stream()
                    .map(HarvestRecord::getActualYield)
                    .filter(yield -> yield != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.debug("项目{}总产量: {}", projectId, totalYield);
            return totalYield;
        } catch (Exception e) {
            log.error("统计项目总产量失败", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal sumYieldByUser(Long userId) {
        if (userId == null) {
            return BigDecimal.ZERO;
        }
        
        log.debug("统计用户总产量: userId={}", userId);
        try {
            LambdaQueryWrapper<HarvestRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HarvestRecord::getUserId, userId);
            wrapper.isNotNull(HarvestRecord::getActualYield);
            wrapper.select(HarvestRecord::getActualYield);
            
            List<HarvestRecord> records = list(wrapper);
            BigDecimal totalYield = records.stream()
                    .map(HarvestRecord::getActualYield)
                    .filter(yield -> yield != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.debug("用户{}总产量: {}", userId, totalYield);
            return totalYield;
        } catch (Exception e) {
            log.error("统计用户总产量失败", e);
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
            statistics.put("totalRecords", countRecordsByProject(projectId));
            statistics.put("totalYield", sumYieldByProject(projectId));
            
            // 质量等级统计
            Map<String, Integer> qualityStats = new HashMap<>();
            qualityStats.put("premium", countRecordsByQuality(projectId, "优质"));
            qualityStats.put("good", countRecordsByQuality(projectId, "良好"));
            qualityStats.put("average", countRecordsByQuality(projectId, "一般"));
            statistics.put("qualityStatistics", qualityStats);
            
            // 平均产量
            int recordCount = countRecordsByProject(projectId);
            if (recordCount > 0) {
                BigDecimal avgYield = sumYieldByProject(projectId).divide(
                    new BigDecimal(recordCount), 2, BigDecimal.ROUND_HALF_UP);
                statistics.put("averageYield", avgYield);
            } else {
                statistics.put("averageYield", BigDecimal.ZERO);
            }
            
            // 最近收获时间
            HarvestRecord latestRecord = getLatestRecordByProject(projectId);
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
    public Object getUserHarvestStatistics(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        log.debug("获取用户收获统计信息: userId={}", userId);
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 基础统计
            statistics.put("totalRecords", countRecordsByUser(userId));
            statistics.put("totalYield", sumYieldByUser(userId));
            
            // 最近收获记录
            List<HarvestRecord> recentRecords = getRecentRecordsByUser(userId, 5);
            statistics.put("recentRecords", recentRecords);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取用户收获统计信息失败", e);
            throw new BusinessException("获取用户收获统计信息失败");
        }
    }

    @Override
    public List<HarvestRecord> getRecentRecords(Long projectId, Integer days) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        if (days == null || days <= 0) {
            days = 7; // 默认7天
        }
        
        log.debug("获取项目最近收获记录: projectId={}, days={}", projectId, days);
        try {
            LocalDate startDate = LocalDate.now().minusDays(days);
            LocalDate endDate = LocalDate.now();
            
            return getRecordsByDateRange(projectId, startDate, endDate);
        } catch (Exception e) {
            log.error("获取项目最近收获记录失败", e);
            throw new BusinessException("获取最近收获记录失败");
        }
    }

    /**
     * 验证收获记录必填字段
     */
    private void validateRecordFields(HarvestRecord record) {
        if (record.getProjectId() == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        if (record.getUnitId() == null) {
            throw new BusinessException("单元ID不能为空");
        }
        
        if (record.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        if (record.getActualYield() == null || record.getActualYield().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("实际产量不能为空且不能小于0");
        }
    }

    /**
     * 验证关联数据是否存在
     */
    private void validateRelatedData(HarvestRecord record) {
        // 验证项目单元是否存在
        if (projectUnitService.getById(record.getUnitId()) == null) {
            throw new BusinessException("项目单元不存在");
        }
        
        // TODO: 验证用户是否存在
    }

    /**
     * 更新认养记录的收获信息
     */
    private void updateAdoptionRecordHarvest(HarvestRecord harvestRecord) {
        try {
            // 根据单元ID查找认养记录
            AdoptionRecord adoptionRecord = adoptionRecordService.getRecordByUnitId(harvestRecord.getUnitId());
            if (adoptionRecord != null) {
                // 更新认养记录的收获信息
                adoptionRecordService.completeHarvest(
                    adoptionRecord.getId(), 
                    harvestRecord.getActualYield(), 
                    harvestRecord.getQualityGrade()
                );
            }
        } catch (Exception e) {
            log.error("更新认养记录收获信息失败", e);
            // 这里不抛出异常，避免影响收获记录的创建
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

    /**
     * 获取项目最新收获记录
     */
    private HarvestRecord getLatestRecordByProject(Long projectId) {
        LambdaQueryWrapper<HarvestRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HarvestRecord::getProjectId, projectId);
        wrapper.orderByDesc(HarvestRecord::getHarvestDate);
        wrapper.last("LIMIT 1");
        
        List<HarvestRecord> records = list(wrapper);
        return records.isEmpty() ? null : records.get(0);
    }

    /**
     * 获取用户最近收获记录
     */
    private List<HarvestRecord> getRecentRecordsByUser(Long userId, Integer limit) {
        LambdaQueryWrapper<HarvestRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HarvestRecord::getUserId, userId);
        wrapper.orderByDesc(HarvestRecord::getHarvestDate);
        wrapper.last("LIMIT " + limit);
        
        return list(wrapper);
    }
}
