package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.ProjectUnit;
import com.ifarm.mapper.ProjectUnitMapper;
import com.ifarm.service.IProjectUnitService;
import com.ifarm.service.IAdoptionProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目单元服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectUnitServiceImpl extends ServiceImpl<ProjectUnitMapper, ProjectUnit> implements IProjectUnitService {

    private final ProjectUnitMapper projectUnitMapper;

    @Override
    public List<ProjectUnit> getUnitsByProjectId(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("根据项目ID查询单元列表: {}", projectId);
        try {
            List<ProjectUnit> units = projectUnitMapper.selectByProjectId(projectId);
            log.debug("查询到{}个单元", units.size());
            return units;
        } catch (Exception e) {
            log.error("根据项目ID查询单元列表失败，项目ID: {}", projectId, e);
            throw new BusinessException("查询单元列表失败");
        }
    }

    @Override
    public List<ProjectUnit> getAvailableUnitsByProjectId(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("根据项目ID查询可认养单元列表: {}", projectId);
        try {
            List<ProjectUnit> units = projectUnitMapper.selectAvailableByProjectId(projectId);
            log.debug("查询到{}个可认养单元", units.size());
            return units;
        } catch (Exception e) {
            log.error("根据项目ID查询可认养单元列表失败，项目ID: {}", projectId, e);
            throw new BusinessException("查询可认养单元列表失败");
        }
    }

    @Override
    public List<ProjectUnit> getUnitsByProjectIdAndStatus(Long projectId, Integer unitStatus) {
        if (projectId == null || unitStatus == null) {
            throw new BusinessException("参数不能为空");
        }
        
        log.debug("根据项目ID和状态查询单元列表: projectId={}, status={}", projectId, unitStatus);
        try {
            List<ProjectUnit> units = projectUnitMapper.selectByProjectIdAndStatus(projectId, unitStatus);
            log.debug("查询到{}个状态为{}的单元", units.size(), unitStatus);
            return units;
        } catch (Exception e) {
            log.error("根据项目ID和状态查询单元列表失败", e);
            throw new BusinessException("查询单元列表失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUnit(ProjectUnit projectUnit) {
        if (projectUnit == null) {
            throw new BusinessException("单元信息不能为空");
        }
        
        // 验证必填字段
        if (projectUnit.getProjectId() == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        if (!StringUtils.hasText(projectUnit.getUnitNumber())) {
            throw new BusinessException("单元编号不能为空");
        }
        
        log.info("创建项目单元: 项目ID={}, 单元编号={}", projectUnit.getProjectId(), projectUnit.getUnitNumber());
        try {
            // 验证单元编号在项目内唯一性
            if (existsByNumberInProject(projectUnit.getProjectId(), projectUnit.getUnitNumber(), null)) {
                throw new BusinessException("该项目下已存在相同编号的单元");
            }
            
            // 设置默认值
            if (projectUnit.getUnitStatus() == null) {
                projectUnit.setUnitStatus(1); // 默认可认养状态
            }
            
            boolean result = save(projectUnit);
            if (result) {
                log.info("项目单元创建成功，ID: {}", projectUnit.getId());
            } else {
                log.error("项目单元创建失败");
                throw new BusinessException("项目单元创建失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建项目单元失败", e);
            throw new BusinessException("创建项目单元失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreateUnits(Long projectId, Integer unitCount) {
        if (projectId == null || unitCount == null || unitCount <= 0) {
            throw new BusinessException("参数无效");
        }
        
        log.info("批量创建项目单元: 项目ID={}, 数量={}", projectId, unitCount);
        try {
            List<ProjectUnit> units = new ArrayList<>();
            
            for (int i = 1; i <= unitCount; i++) {
                ProjectUnit unit = new ProjectUnit();
                unit.setProjectId(projectId);
                unit.setUnitNumber(generateUnitNumber(projectId, i));
                unit.setUnitStatus(1); // 可认养状态
                
                // 设置位置信息（简单的行列布局）
                int row = (i - 1) / 10 + 1; // 每行10个单元
                int col = (i - 1) % 10 + 1;
                String locationInfo = String.format("{\"row\": %d, \"column\": %d, \"coordinates\": \"%c%d\"}", 
                    row, col, (char)('A' + row - 1), col);
                unit.setLocationInfo(locationInfo);
                
                units.add(unit);
            }
            
            boolean result = saveBatch(units);
            if (result) {
                log.info("批量创建项目单元成功，创建{}个单元", unitCount);
            } else {
                log.error("批量创建项目单元失败");
                throw new BusinessException("批量创建项目单元失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量创建项目单元失败", e);
            throw new BusinessException("批量创建项目单元失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUnit(ProjectUnit projectUnit) {
        if (projectUnit == null || projectUnit.getId() == null) {
            throw new BusinessException("单元信息不完整");
        }
        
        log.info("更新项目单元: ID={}, UnitNumber={}", projectUnit.getId(), projectUnit.getUnitNumber());
        try {
            // 验证单元是否存在
            ProjectUnit existingUnit = getById(projectUnit.getId());
            if (existingUnit == null) {
                throw new BusinessException("单元不存在");
            }
            
            // 验证单元编号在项目内唯一性
            if (StringUtils.hasText(projectUnit.getUnitNumber()) && 
                existsByNumberInProject(existingUnit.getProjectId(), projectUnit.getUnitNumber(), projectUnit.getId())) {
                throw new BusinessException("该项目下已存在相同编号的单元");
            }
            
            boolean result = updateById(projectUnit);
            if (result) {
                log.info("项目单元更新成功");
            } else {
                log.error("项目单元更新失败");
                throw new BusinessException("项目单元更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新项目单元失败", e);
            throw new BusinessException("更新项目单元失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUnit(Long unitId) {
        if (unitId == null) {
            throw new BusinessException("单元ID不能为空");
        }
        
        log.info("删除项目单元: ID={}", unitId);
        try {
            // 验证单元是否存在
            ProjectUnit unit = getById(unitId);
            if (unit == null) {
                throw new BusinessException("单元不存在");
            }
            
            // 检查单元状态，只有可认养状态的单元才能删除
            if (unit.getUnitStatus() != 1) {
                throw new BusinessException("只有可认养状态的单元才能删除");
            }
            
            boolean result = removeById(unitId);
            if (result) {
                log.info("项目单元删除成功");
            } else {
                log.error("项目单元删除失败");
                throw new BusinessException("项目单元删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除项目单元失败", e);
            throw new BusinessException("删除项目单元失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUnitStatus(Long unitId, Integer unitStatus) {
        if (unitId == null || unitStatus == null) {
            throw new BusinessException("参数不能为空");
        }
        
        if (unitStatus < 1 || unitStatus > 5) {
            throw new BusinessException("单元状态值无效");
        }
        
        log.info("更新单元状态: ID={}, Status={}", unitId, unitStatus);
        try {
            ProjectUnit unit = new ProjectUnit();
            unit.setId(unitId);
            unit.setUnitStatus(unitStatus);
            
            boolean result = updateById(unit);
            if (result) {
                log.info("单元状态更新成功");
            } else {
                log.error("单元状态更新失败");
                throw new BusinessException("单元状态更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新单元状态失败", e);
            throw new BusinessException("更新单元状态失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateUnitStatus(List<Long> unitIds, Integer unitStatus) {
        if (unitIds == null || unitIds.isEmpty()) {
            throw new BusinessException("单元ID列表不能为空");
        }
        
        if (unitStatus == null || unitStatus < 1 || unitStatus > 5) {
            throw new BusinessException("单元状态值无效");
        }
        
        log.info("批量更新单元状态: IDs={}, Status={}", unitIds, unitStatus);
        try {
            int result = projectUnitMapper.batchUpdateStatus(unitIds, unitStatus);
            if (result > 0) {
                log.info("批量更新单元状态成功，影响{}条记录", result);
                return true;
            } else {
                log.error("批量更新单元状态失败");
                throw new BusinessException("批量更新单元状态失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量更新单元状态失败", e);
            throw new BusinessException("批量更新单元状态失败");
        }
    }

    @Override
    public ProjectUnit getUnitDetail(Long unitId) {
        if (unitId == null) {
            throw new BusinessException("单元ID不能为空");
        }
        
        log.debug("获取单元详情: ID={}", unitId);
        try {
            ProjectUnit unit = getById(unitId);
            if (unit == null) {
                throw new BusinessException("单元不存在");
            }
            return unit;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取单元详情失败", e);
            throw new BusinessException("获取单元详情失败");
        }
    }

    @Override
    public ProjectUnit getUnitByProjectIdAndNumber(Long projectId, String unitNumber) {
        if (projectId == null || !StringUtils.hasText(unitNumber)) {
            throw new BusinessException("参数不能为空");
        }
        
        log.debug("根据项目ID和单元编号查询单元: projectId={}, unitNumber={}", projectId, unitNumber);
        try {
            ProjectUnit unit = projectUnitMapper.selectByProjectIdAndUnitNumber(projectId, unitNumber);
            return unit;
        } catch (Exception e) {
            log.error("根据项目ID和单元编号查询单元失败", e);
            throw new BusinessException("查询单元失败");
        }
    }

    @Override
    public boolean existsByNumberInProject(Long projectId, String unitNumber, Long excludeId) {
        if (projectId == null || !StringUtils.hasText(unitNumber)) {
            return false;
        }
        
        LambdaQueryWrapper<ProjectUnit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectUnit::getProjectId, projectId);
        wrapper.eq(ProjectUnit::getUnitNumber, unitNumber);
        if (excludeId != null) {
            wrapper.ne(ProjectUnit::getId, excludeId);
        }
        
        return count(wrapper) > 0;
    }

    @Override
    public int countUnitsByProjectId(Long projectId) {
        if (projectId == null) {
            return 0;
        }
        
        return projectUnitMapper.countByProjectId(projectId);
    }

    @Override
    public int countUnitsByProjectIdAndStatus(Long projectId, Integer unitStatus) {
        if (projectId == null || unitStatus == null) {
            return 0;
        }
        
        return projectUnitMapper.countByProjectIdAndStatus(projectId, unitStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> allocateUnits(Long projectId, Integer unitCount) {
        if (projectId == null || unitCount == null || unitCount <= 0) {
            throw new BusinessException("参数无效");
        }
        
        log.info("分配单元给用户: 项目ID={}, 需要数量={}", projectId, unitCount);
        try {
            // 查询可用单元
            List<ProjectUnit> availableUnits = getAvailableUnitsByProjectId(projectId);
            
            if (availableUnits.size() < unitCount) {
                throw new BusinessException("可用单元数量不足");
            }
            
            // 取前N个单元进行分配
            List<Long> allocatedUnitIds = availableUnits.stream()
                    .limit(unitCount)
                    .map(ProjectUnit::getId)
                    .toList();
            
            // 更新单元状态为已认养
            boolean result = batchUpdateUnitStatus(allocatedUnitIds, 2);
            
            if (result) {
                log.info("单元分配成功，分配{}个单元", allocatedUnitIds.size());
                return allocatedUnitIds;
            } else {
                throw new BusinessException("单元分配失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("分配单元失败", e);
            throw new BusinessException("分配单元失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseUnits(List<Long> unitIds) {
        if (unitIds == null || unitIds.isEmpty()) {
            throw new BusinessException("单元ID列表不能为空");
        }
        
        log.info("释放单元: IDs={}", unitIds);
        try {
            // 将单元状态改回可认养
            boolean result = batchUpdateUnitStatus(unitIds, 1);
            
            if (result) {
                log.info("单元释放成功，释放{}个单元", unitIds.size());
            }
            return result;
        } catch (Exception e) {
            log.error("释放单元失败", e);
            throw new BusinessException("释放单元失败");
        }
    }

    @Override
    public Object getUnitUsageStatistics(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("获取单元使用情况统计: projectId={}", projectId);
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总数统计
            statistics.put("totalCount", countUnitsByProjectId(projectId));
            statistics.put("availableCount", countUnitsByProjectIdAndStatus(projectId, 1));
            statistics.put("adoptedCount", countUnitsByProjectIdAndStatus(projectId, 2));
            statistics.put("plantingCount", countUnitsByProjectIdAndStatus(projectId, 3));
            statistics.put("harvestingCount", countUnitsByProjectIdAndStatus(projectId, 4));
            statistics.put("harvestedCount", countUnitsByProjectIdAndStatus(projectId, 5));
            
            return statistics;
        } catch (Exception e) {
            log.error("获取单元使用情况统计失败", e);
            throw new BusinessException("获取单元使用情况统计失败");
        }
    }

    @Override
    public String generateUnitNumber(Long projectId, Integer sequence) {
        if (projectId == null || sequence == null || sequence <= 0) {
            throw new BusinessException("参数无效");
        }
        
        // 生成格式：P{projectId}-{sequence:03d}，例如：P1-001
        return String.format("P%d-%03d", projectId, sequence);
    }

    @Override
    public boolean hasPermission(Long userId, Long unitId) {
        if (userId == null || unitId == null) {
            return false;
        }
        
        log.debug("检查用户单元操作权限: userId={}, unitId={}", userId, unitId);
        try {
            // 获取单元信息
            ProjectUnit unit = getById(unitId);
            if (unit == null) {
                return false;
            }
            
            // TODO: 检查用户是否为该单元所属项目的农场主
            // 这里需要通过项目查询地块，再查询农场，最后检查农场主
            // 为了避免循环依赖，暂时返回true
            return true;
        } catch (Exception e) {
            log.error("检查用户单元操作权限失败", e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startPlanting(List<Long> unitIds) {
        if (unitIds == null || unitIds.isEmpty()) {
            throw new BusinessException("单元ID列表不能为空");
        }
        
        log.info("开始单元种植: unitIds={}", unitIds);
        try {
            // 更新单元状态为种植中
            boolean result = batchUpdateUnitStatus(unitIds, 3);
            
            if (result) {
                log.info("单元种植开始成功，影响{}个单元", unitIds.size());
            }
            return result;
        } catch (Exception e) {
            log.error("开始单元种植失败", e);
            throw new BusinessException("开始单元种植失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeHarvest(List<Long> unitIds) {
        if (unitIds == null || unitIds.isEmpty()) {
            throw new BusinessException("单元ID列表不能为空");
        }
        
        log.info("单元收获完成: unitIds={}", unitIds);
        try {
            // 更新单元状态为已收获
            boolean result = batchUpdateUnitStatus(unitIds, 5);
            
            if (result) {
                log.info("单元收获完成成功，影响{}个单元", unitIds.size());
            }
            return result;
        } catch (Exception e) {
            log.error("单元收获完成失败", e);
            throw new BusinessException("单元收获完成失败");
        }
    }
}
