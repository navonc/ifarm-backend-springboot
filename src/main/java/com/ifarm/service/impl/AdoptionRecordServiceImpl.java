package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.AdoptionOrder;
import com.ifarm.entity.AdoptionRecord;
import com.ifarm.mapper.AdoptionRecordMapper;
import com.ifarm.service.IAdoptionOrderService;
import com.ifarm.service.IAdoptionRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户认养记录服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdoptionRecordServiceImpl extends ServiceImpl<AdoptionRecordMapper, AdoptionRecord> implements IAdoptionRecordService {

    private final AdoptionRecordMapper adoptionRecordMapper;
    private final IAdoptionOrderService adoptionOrderService;

    @Override
    public List<AdoptionRecord> getRecordsByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        log.debug("根据用户ID查询认养记录列表: {}", userId);
        try {
            List<AdoptionRecord> records = adoptionRecordMapper.selectByUserId(userId);
            log.debug("查询到{}条认养记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据用户ID查询认养记录列表失败，用户ID: {}", userId, e);
            throw new BusinessException("查询认养记录列表失败");
        }
    }

    @Override
    public List<AdoptionRecord> getRecordsByProjectId(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("根据项目ID查询认养记录列表: {}", projectId);
        try {
            List<AdoptionRecord> records = adoptionRecordMapper.selectByProjectId(projectId);
            log.debug("查询到{}条认养记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据项目ID查询认养记录列表失败，项目ID: {}", projectId, e);
            throw new BusinessException("查询认养记录列表失败");
        }
    }

    @Override
    public List<AdoptionRecord> getRecordsByOrderId(Long orderId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        
        log.debug("根据订单ID查询认养记录列表: {}", orderId);
        try {
            List<AdoptionRecord> records = adoptionRecordMapper.selectByOrderId(orderId);
            log.debug("查询到{}条认养记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("根据订单ID查询认养记录列表失败，订单ID: {}", orderId, e);
            throw new BusinessException("查询认养记录列表失败");
        }
    }

    @Override
    public AdoptionRecord getRecordByUnitId(Long unitId) {
        if (unitId == null) {
            throw new BusinessException("单元ID不能为空");
        }
        
        log.debug("根据单元ID查询认养记录: {}", unitId);
        try {
            return adoptionRecordMapper.selectByUnitId(unitId);
        } catch (Exception e) {
            log.error("根据单元ID查询认养记录失败，单元ID: {}", unitId, e);
            throw new BusinessException("查询认养记录失败");
        }
    }

    @Override
    public IPage<AdoptionRecord> getUserAdoptionPage(Page<AdoptionRecord> page, Long userId, Integer adoptionStatus) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        log.debug("分页查询用户认养记录: userId={}, adoptionStatus={}", userId, adoptionStatus);
        try {
            IPage<AdoptionRecord> result = adoptionRecordMapper.selectUserAdoptionPage(page, userId, adoptionStatus);
            log.debug("查询到{}条认养记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("分页查询用户认养记录失败", e);
            throw new BusinessException("查询认养记录失败");
        }
    }

    @Override
    public List<AdoptionRecord> getRecordsByStatus(Integer adoptionStatus) {
        if (adoptionStatus == null) {
            throw new BusinessException("认养状态不能为空");
        }
        
        log.debug("根据认养状态查询记录列表: {}", adoptionStatus);
        try {
            List<AdoptionRecord> records = adoptionRecordMapper.selectByAdoptionStatus(adoptionStatus);
            log.debug("查询到{}条状态为{}的认养记录", records.size(), adoptionStatus);
            return records;
        } catch (Exception e) {
            log.error("根据认养状态查询记录列表失败，状态: {}", adoptionStatus, e);
            throw new BusinessException("查询认养记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRecords(Long orderId, List<Long> unitIds) {
        if (orderId == null || unitIds == null || unitIds.isEmpty()) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("创建认养记录: 订单ID={}, 单元数量={}", orderId, unitIds.size());
        try {
            // 获取订单信息
            AdoptionOrder order = adoptionOrderService.getById(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            // 创建认养记录
            List<AdoptionRecord> records = new ArrayList<>();
            LocalDateTime adoptionDate = LocalDateTime.now();
            
            for (Long unitId : unitIds) {
                AdoptionRecord record = new AdoptionRecord();
                record.setOrderId(orderId);
                record.setUserId(order.getUserId());
                record.setProjectId(order.getProjectId());
                record.setUnitId(unitId);
                record.setAdoptionStatus(1); // 已认养状态
                record.setAdoptionDate(adoptionDate);
                
                records.add(record);
            }
            
            boolean result = saveBatch(records);
            if (result) {
                log.info("认养记录创建成功，创建{}条记录", records.size());
            } else {
                log.error("认养记录创建失败");
                throw new BusinessException("认养记录创建失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建认养记录失败", e);
            throw new BusinessException("创建认养记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRecord(AdoptionRecord adoptionRecord) {
        if (adoptionRecord == null || adoptionRecord.getId() == null) {
            throw new BusinessException("认养记录信息不完整");
        }
        
        log.info("更新认养记录: ID={}", adoptionRecord.getId());
        try {
            // 验证记录是否存在
            AdoptionRecord existingRecord = getById(adoptionRecord.getId());
            if (existingRecord == null) {
                throw new BusinessException("认养记录不存在");
            }
            
            boolean result = updateById(adoptionRecord);
            if (result) {
                log.info("认养记录更新成功");
            } else {
                log.error("认养记录更新失败");
                throw new BusinessException("认养记录更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新认养记录失败", e);
            throw new BusinessException("更新认养记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAdoptionStatus(Long recordId, Integer adoptionStatus) {
        if (recordId == null || adoptionStatus == null) {
            throw new BusinessException("参数不能为空");
        }
        
        if (adoptionStatus < 1 || adoptionStatus > 5) {
            throw new BusinessException("认养状态值无效");
        }
        
        log.info("更新认养状态: ID={}, Status={}", recordId, adoptionStatus);
        try {
            // 验证状态流转规则
            AdoptionRecord existingRecord = getById(recordId);
            if (existingRecord == null) {
                throw new BusinessException("认养记录不存在");
            }
            
            validateStatusTransition(existingRecord.getAdoptionStatus(), adoptionStatus);
            
            AdoptionRecord record = new AdoptionRecord();
            record.setId(recordId);
            record.setAdoptionStatus(adoptionStatus);
            
            // 根据状态设置相应的时间字段
            LocalDateTime now = LocalDateTime.now();
            switch (adoptionStatus) {
                case 2: // 种植中
                    record.setPlantingDate(now);
                    break;
                case 3: // 待收获
                    // 不设置时间，等待实际收获时设置
                    break;
                case 4: // 已收获
                    record.setHarvestDate(now);
                    break;
                case 5: // 已完成
                    if (existingRecord.getHarvestDate() == null) {
                        record.setHarvestDate(now);
                    }
                    break;
            }
            
            boolean result = updateById(record);
            if (result) {
                log.info("认养状态更新成功");
            } else {
                log.error("认养状态更新失败");
                throw new BusinessException("认养状态更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新认养状态失败", e);
            throw new BusinessException("更新认养状态失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateAdoptionStatus(List<Long> recordIds, Integer adoptionStatus) {
        if (recordIds == null || recordIds.isEmpty()) {
            throw new BusinessException("记录ID列表不能为空");
        }
        
        if (adoptionStatus == null || adoptionStatus < 1 || adoptionStatus > 5) {
            throw new BusinessException("认养状态值无效");
        }
        
        log.info("批量更新认养状态: IDs={}, Status={}", recordIds, adoptionStatus);
        try {
            int result = adoptionRecordMapper.batchUpdateAdoptionStatus(recordIds, adoptionStatus);
            if (result > 0) {
                log.info("批量更新认养状态成功，影响{}条记录", result);
                return true;
            } else {
                log.error("批量更新认养状态失败");
                throw new BusinessException("批量更新认养状态失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量更新认养状态失败", e);
            throw new BusinessException("批量更新认养状态失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startPlanting(List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            throw new BusinessException("记录ID列表不能为空");
        }
        
        log.info("开始种植: recordIds={}", recordIds);
        try {
            // 更新认养状态为种植中
            boolean result = batchUpdateAdoptionStatus(recordIds, 2);
            
            if (result) {
                log.info("开始种植成功，影响{}条记录", recordIds.size());
            }
            return result;
        } catch (Exception e) {
            log.error("开始种植失败", e);
            throw new BusinessException("开始种植失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startHarvesting(List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            throw new BusinessException("记录ID列表不能为空");
        }
        
        log.info("开始收获: recordIds={}", recordIds);
        try {
            // 更新认养状态为待收获
            boolean result = batchUpdateAdoptionStatus(recordIds, 3);
            
            if (result) {
                log.info("开始收获成功，影响{}条记录", recordIds.size());
            }
            return result;
        } catch (Exception e) {
            log.error("开始收获失败", e);
            throw new BusinessException("开始收获失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeHarvest(Long recordId, BigDecimal actualYield, String qualityGrade) {
        if (recordId == null) {
            throw new BusinessException("记录ID不能为空");
        }
        
        log.info("完成收获: recordId={}, actualYield={}, qualityGrade={}", recordId, actualYield, qualityGrade);
        try {
            // 验证记录是否存在
            AdoptionRecord existingRecord = getById(recordId);
            if (existingRecord == null) {
                throw new BusinessException("认养记录不存在");
            }
            
            // 更新记录信息
            AdoptionRecord record = new AdoptionRecord();
            record.setId(recordId);
            record.setAdoptionStatus(4); // 已收获状态
            record.setHarvestDate(LocalDateTime.now());
            
            if (actualYield != null) {
                record.setActualYield(actualYield);
            }
            
            if (qualityGrade != null) {
                record.setQualityGrade(qualityGrade);
            }
            
            boolean result = updateById(record);
            if (result) {
                log.info("完成收获成功");
            } else {
                log.error("完成收获失败");
                throw new BusinessException("完成收获失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("完成收获失败", e);
            throw new BusinessException("完成收获失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCompleteHarvest(List<Long> recordIds, BigDecimal actualYield, String qualityGrade) {
        if (recordIds == null || recordIds.isEmpty()) {
            throw new BusinessException("记录ID列表不能为空");
        }
        
        log.info("批量完成收获: recordIds={}, actualYield={}, qualityGrade={}", recordIds, actualYield, qualityGrade);
        try {
            int successCount = 0;
            
            for (Long recordId : recordIds) {
                try {
                    if (completeHarvest(recordId, actualYield, qualityGrade)) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("批量完成收获失败，记录ID: {}", recordId, e);
                }
            }
            
            boolean result = successCount > 0;
            if (result) {
                log.info("批量完成收获成功，成功{}条，总共{}条", successCount, recordIds.size());
            } else {
                log.error("批量完成收获失败");
                throw new BusinessException("批量完成收获失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量完成收获失败", e);
            throw new BusinessException("批量完成收获失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeAdoption(Long recordId) {
        if (recordId == null) {
            throw new BusinessException("记录ID不能为空");
        }
        
        log.info("完成认养: recordId={}", recordId);
        try {
            // 更新认养状态为已完成
            boolean result = updateAdoptionStatus(recordId, 5);
            
            if (result) {
                log.info("完成认养成功");
            }
            return result;
        } catch (Exception e) {
            log.error("完成认养失败", e);
            throw new BusinessException("完成认养失败");
        }
    }

    @Override
    public AdoptionRecord getRecordDetail(Long recordId) {
        if (recordId == null) {
            throw new BusinessException("记录ID不能为空");
        }
        
        log.debug("获取认养记录详情: ID={}", recordId);
        try {
            AdoptionRecord record = adoptionRecordMapper.selectRecordDetail(recordId);
            if (record == null) {
                throw new BusinessException("认养记录不存在");
            }
            return record;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取认养记录详情失败", e);
            throw new BusinessException("获取认养记录详情失败");
        }
    }

    @Override
    public boolean hasPermission(Long userId, Long recordId) {
        if (userId == null || recordId == null) {
            return false;
        }
        
        log.debug("检查用户认养记录操作权限: userId={}, recordId={}", userId, recordId);
        try {
            AdoptionRecord record = getById(recordId);
            if (record == null) {
                return false;
            }
            
            // 检查记录是否属于该用户
            return record.getUserId().equals(userId);
        } catch (Exception e) {
            log.error("检查用户认养记录操作权限失败", e);
            return false;
        }
    }

    @Override
    public int countUserAdoptions(Long userId, Integer adoptionStatus) {
        if (userId == null) {
            return 0;
        }
        
        return adoptionRecordMapper.countUserAdoptions(userId, adoptionStatus);
    }

    @Override
    public BigDecimal sumUserYield(Long userId) {
        if (userId == null) {
            return BigDecimal.ZERO;
        }
        
        log.debug("统计用户认养总产量: userId={}", userId);
        try {
            LambdaQueryWrapper<AdoptionRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdoptionRecord::getUserId, userId);
            wrapper.isNotNull(AdoptionRecord::getActualYield);
            wrapper.select(AdoptionRecord::getActualYield);
            
            List<AdoptionRecord> records = list(wrapper);
            BigDecimal totalYield = records.stream()
                    .map(AdoptionRecord::getActualYield)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.debug("用户{}总产量: {}", userId, totalYield);
            return totalYield;
        } catch (Exception e) {
            log.error("统计用户认养总产量失败", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Object getUserAdoptionStatistics(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        log.debug("获取用户认养统计信息: userId={}", userId);
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 状态统计
            statistics.put("totalCount", countUserAdoptions(userId, null));
            statistics.put("adoptedCount", countUserAdoptions(userId, 1));
            statistics.put("plantingCount", countUserAdoptions(userId, 2));
            statistics.put("harvestingCount", countUserAdoptions(userId, 3));
            statistics.put("harvestedCount", countUserAdoptions(userId, 4));
            statistics.put("completedCount", countUserAdoptions(userId, 5));
            
            // 产量统计
            statistics.put("totalYield", sumUserYield(userId));
            
            return statistics;
        } catch (Exception e) {
            log.error("获取用户认养统计信息失败", e);
            throw new BusinessException("获取用户认养统计信息失败");
        }
    }

    @Override
    public Object getProjectAdoptionStatistics(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("获取项目认养统计信息: projectId={}", projectId);
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            LambdaQueryWrapper<AdoptionRecord> baseWrapper = new LambdaQueryWrapper<>();
            baseWrapper.eq(AdoptionRecord::getProjectId, projectId);
            
            // 状态统计
            statistics.put("totalCount", count(baseWrapper));
            statistics.put("adoptedCount", count(baseWrapper.clone().eq(AdoptionRecord::getAdoptionStatus, 1)));
            statistics.put("plantingCount", count(baseWrapper.clone().eq(AdoptionRecord::getAdoptionStatus, 2)));
            statistics.put("harvestingCount", count(baseWrapper.clone().eq(AdoptionRecord::getAdoptionStatus, 3)));
            statistics.put("harvestedCount", count(baseWrapper.clone().eq(AdoptionRecord::getAdoptionStatus, 4)));
            statistics.put("completedCount", count(baseWrapper.clone().eq(AdoptionRecord::getAdoptionStatus, 5)));
            
            return statistics;
        } catch (Exception e) {
            log.error("获取项目认养统计信息失败", e);
            throw new BusinessException("获取项目认养统计信息失败");
        }
    }

    /**
     * 验证认养状态流转规则
     * 
     * @param currentStatus 当前状态
     * @param newStatus 新状态
     */
    private void validateStatusTransition(Integer currentStatus, Integer newStatus) {
        if (currentStatus == null || newStatus == null) {
            throw new BusinessException("状态不能为空");
        }
        
        if (currentStatus.equals(newStatus)) {
            return; // 状态未变更
        }
        
        // 定义允许的状态流转规则
        // 1-已认养 -> 2-种植中
        // 2-种植中 -> 3-待收获
        // 3-待收获 -> 4-已收获
        // 4-已收获 -> 5-已完成
        
        boolean validTransition = switch (currentStatus) {
            case 1 -> // 已认养
                    newStatus == 2;
            case 2 -> // 种植中
                    newStatus == 3;
            case 3 -> // 待收获
                    newStatus == 4;
            case 4 -> // 已收获
                    newStatus == 5;
            case 5 -> // 已完成
                    false;
            default -> false; // 已完成状态不能变更
        };

        if (!validTransition) {
            throw new BusinessException("无效的状态流转");
        }
    }

    @Override
    public List<AdoptionRecord> getUpcomingHarvestRecords(Integer days) {
        if (days == null || days <= 0) {
            days = 7; // 默认7天内
        }

        log.debug("查询即将收获的认养记录，提前天数: {}", days);
        try {
            // 查询种植中状态的记录，且预计收获时间在指定天数内
            LambdaQueryWrapper<AdoptionRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdoptionRecord::getAdoptionStatus, 2); // 种植中状态
            // TODO: 根据种植日期和作物生长周期计算预计收获时间
            wrapper.orderByAsc(AdoptionRecord::getPlantingDate);

            List<AdoptionRecord> records = list(wrapper);
            log.debug("查询到{}条即将收获的记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("查询即将收获的认养记录失败", e);
            throw new BusinessException("查询即将收获的认养记录失败");
        }
    }

    @Override
    public List<AdoptionRecord> getCompletedRecords(Long userId, Integer limit) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        if (limit == null || limit <= 0) {
            limit = 10;
        }

        log.debug("查询用户已完成的认养记录: userId={}, limit={}", userId, limit);
        try {
            LambdaQueryWrapper<AdoptionRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdoptionRecord::getUserId, userId);
            wrapper.eq(AdoptionRecord::getAdoptionStatus, 5); // 已完成状态
            wrapper.orderByDesc(AdoptionRecord::getHarvestDate);
            wrapper.last("LIMIT " + limit);

            List<AdoptionRecord> records = list(wrapper);
            log.debug("查询到{}条已完成的记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("查询用户已完成的认养记录失败", e);
            throw new BusinessException("查询用户已完成的认养记录失败");
        }
    }

    @Override
    public Object getAdoptionCertificate(Long recordId) {
        if (recordId == null) {
            throw new BusinessException("记录ID不能为空");
        }

        log.debug("获取认养证书信息: recordId={}", recordId);
        try {
            // 获取认养记录详情
            AdoptionRecord record = getRecordDetail(recordId);
            if (record == null) {
                throw new BusinessException("认养记录不存在");
            }

            // 只有已完成的记录才能生成证书
            if (record.getAdoptionStatus() != 5) {
                throw new BusinessException("只有已完成的认养记录才能生成证书");
            }

            // 构建证书信息
            Map<String, Object> certificate = new HashMap<>();
            certificate.put("recordId", record.getId());
            certificate.put("certificateNo", generateCertificateNo(record.getId()));
            certificate.put("userId", record.getUserId());
            certificate.put("projectId", record.getProjectId());
            certificate.put("unitId", record.getUnitId());
            certificate.put("adoptionDate", record.getAdoptionDate());
            certificate.put("plantingDate", record.getPlantingDate());
            certificate.put("harvestDate", record.getHarvestDate());
            certificate.put("actualYield", record.getActualYield());
            certificate.put("qualityGrade", record.getQualityGrade());
            certificate.put("generateTime", LocalDateTime.now());

            // TODO: 可以添加更多证书信息，如项目名称、作物名称、农场信息等

            log.debug("认养证书信息生成成功");
            return certificate;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取认养证书信息失败", e);
            throw new BusinessException("获取认养证书信息失败");
        }
    }

    /**
     * 生成证书编号
     *
     * @param recordId 记录ID
     * @return 证书编号
     */
    private String generateCertificateNo(Long recordId) {
        // 生成格式：CERT + yyyyMMdd + recordId
        String dateStr = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("CERT%s%06d", dateStr, recordId);
    }
}
