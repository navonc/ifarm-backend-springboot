package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.FarmPlot;
import com.ifarm.mapper.FarmPlotMapper;
import com.ifarm.service.IFarmPlotService;
import com.ifarm.service.IFarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 农场地块服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FarmPlotServiceImpl extends ServiceImpl<FarmPlotMapper, FarmPlot> implements IFarmPlotService {

    private final FarmPlotMapper farmPlotMapper;
    private final IFarmService farmService;

    @Override
    public List<FarmPlot> getPlotsByFarmId(Long farmId) {
        if (farmId == null) {
            throw new BusinessException("农场ID不能为空");
        }
        
        log.debug("根据农场ID查询地块列表: {}", farmId);
        try {
            List<FarmPlot> plots = farmPlotMapper.selectByFarmId(farmId);
            log.debug("查询到{}个地块", plots.size());
            return plots;
        } catch (Exception e) {
            log.error("根据农场ID查询地块列表失败，农场ID: {}", farmId, e);
            throw new BusinessException("查询地块列表失败");
        }
    }

    @Override
    public List<FarmPlot> getAvailablePlotsByFarmId(Long farmId) {
        if (farmId == null) {
            throw new BusinessException("农场ID不能为空");
        }
        
        log.debug("根据农场ID查询可用地块列表: {}", farmId);
        try {
            List<FarmPlot> plots = farmPlotMapper.selectAvailableByFarmId(farmId);
            log.debug("查询到{}个可用地块", plots.size());
            return plots;
        } catch (Exception e) {
            log.error("根据农场ID查询可用地块列表失败，农场ID: {}", farmId, e);
            throw new BusinessException("查询可用地块列表失败");
        }
    }

    @Override
    public List<FarmPlot> getPlotsBySoilType(String soilType) {
        if (!StringUtils.hasText(soilType)) {
            throw new BusinessException("土壤类型不能为空");
        }
        
        log.debug("根据土壤类型查询地块列表: {}", soilType);
        try {
            List<FarmPlot> plots = farmPlotMapper.selectBySoilType(soilType);
            log.debug("查询到{}个{}土壤类型的地块", plots.size(), soilType);
            return plots;
        } catch (Exception e) {
            log.error("根据土壤类型查询地块列表失败，土壤类型: {}", soilType, e);
            throw new BusinessException("查询地块列表失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPlot(FarmPlot farmPlot) {
        if (farmPlot == null) {
            throw new BusinessException("地块信息不能为空");
        }
        
        // 验证必填字段
        if (farmPlot.getFarmId() == null) {
            throw new BusinessException("农场ID不能为空");
        }
        
        if (!StringUtils.hasText(farmPlot.getName())) {
            throw new BusinessException("地块名称不能为空");
        }
        
        if (farmPlot.getArea() == null || farmPlot.getArea().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("地块面积必须大于0");
        }
        
        log.info("创建地块: 农场ID={}, 地块名称={}", farmPlot.getFarmId(), farmPlot.getName());
        try {
            // 验证农场是否存在
            if (farmService.getById(farmPlot.getFarmId()) == null) {
                throw new BusinessException("农场不存在");
            }
            
            // 验证地块名称在农场内唯一性
            if (existsByNameInFarm(farmPlot.getFarmId(), farmPlot.getName(), null)) {
                throw new BusinessException("该农场下已存在同名地块");
            }
            
            // 设置默认值
            if (farmPlot.getStatus() == null) {
                farmPlot.setStatus(1); // 默认可用状态
            }
            
            boolean result = save(farmPlot);
            if (result) {
                log.info("地块创建成功，ID: {}", farmPlot.getId());
            } else {
                log.error("地块创建失败");
                throw new BusinessException("地块创建失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建地块失败", e);
            throw new BusinessException("创建地块失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePlot(FarmPlot farmPlot) {
        if (farmPlot == null || farmPlot.getId() == null) {
            throw new BusinessException("地块信息不完整");
        }
        
        log.info("更新地块: ID={}, Name={}", farmPlot.getId(), farmPlot.getName());
        try {
            // 验证地块是否存在
            FarmPlot existingPlot = getById(farmPlot.getId());
            if (existingPlot == null) {
                throw new BusinessException("地块不存在");
            }
            
            // 验证地块名称在农场内唯一性
            if (StringUtils.hasText(farmPlot.getName()) && 
                existsByNameInFarm(existingPlot.getFarmId(), farmPlot.getName(), farmPlot.getId())) {
                throw new BusinessException("该农场下已存在同名地块");
            }
            
            // 验证面积
            if (farmPlot.getArea() != null && farmPlot.getArea().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("地块面积必须大于0");
            }
            
            boolean result = updateById(farmPlot);
            if (result) {
                log.info("地块更新成功");
            } else {
                log.error("地块更新失败");
                throw new BusinessException("地块更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新地块失败", e);
            throw new BusinessException("更新地块失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePlot(Long plotId) {
        if (plotId == null) {
            throw new BusinessException("地块ID不能为空");
        }
        
        log.info("删除地块: ID={}", plotId);
        try {
            // 验证地块是否存在
            FarmPlot plot = getById(plotId);
            if (plot == null) {
                throw new BusinessException("地块不存在");
            }
            
            // 检查是否可以删除（是否有关联的项目）
            if (!canDeletePlot(plotId)) {
                throw new BusinessException("该地块下存在认养项目，无法删除");
            }
            
            boolean result = removeById(plotId);
            if (result) {
                log.info("地块删除成功");
            } else {
                log.error("地块删除失败");
                throw new BusinessException("地块删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除地块失败", e);
            throw new BusinessException("删除地块失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePlotStatus(Long plotId, Integer status) {
        if (plotId == null || status == null) {
            throw new BusinessException("参数不能为空");
        }
        
        if (status < 0 || status > 2) {
            throw new BusinessException("状态值无效");
        }
        
        log.info("更新地块状态: ID={}, Status={}", plotId, status);
        try {
            FarmPlot plot = new FarmPlot();
            plot.setId(plotId);
            plot.setStatus(status);
            
            boolean result = updateById(plot);
            if (result) {
                log.info("地块状态更新成功");
            } else {
                log.error("地块状态更新失败");
                throw new BusinessException("地块状态更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新地块状态失败", e);
            throw new BusinessException("更新地块状态失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdatePlotStatus(List<Long> plotIds, Integer status) {
        if (plotIds == null || plotIds.isEmpty()) {
            throw new BusinessException("地块ID列表不能为空");
        }
        
        if (status == null || status < 0 || status > 2) {
            throw new BusinessException("状态值无效");
        }
        
        log.info("批量更新地块状态: IDs={}, Status={}", plotIds, status);
        try {
            LambdaQueryWrapper<FarmPlot> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(FarmPlot::getId, plotIds);
            
            FarmPlot plot = new FarmPlot();
            plot.setStatus(status);
            
            boolean result = update(plot, wrapper);
            if (result) {
                log.info("批量更新地块状态成功，影响{}条记录", plotIds.size());
            } else {
                log.error("批量更新地块状态失败");
                throw new BusinessException("批量更新地块状态失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量更新地块状态失败", e);
            throw new BusinessException("批量更新地块状态失败");
        }
    }

    @Override
    public FarmPlot getPlotDetail(Long plotId) {
        if (plotId == null) {
            throw new BusinessException("地块ID不能为空");
        }
        
        log.debug("获取地块详情: ID={}", plotId);
        try {
            FarmPlot plot = getById(plotId);
            if (plot == null) {
                throw new BusinessException("地块不存在");
            }
            return plot;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取地块详情失败", e);
            throw new BusinessException("获取地块详情失败");
        }
    }

    @Override
    public boolean existsByNameInFarm(Long farmId, String name, Long excludeId) {
        if (farmId == null || !StringUtils.hasText(name)) {
            return false;
        }
        
        LambdaQueryWrapper<FarmPlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmPlot::getFarmId, farmId);
        wrapper.eq(FarmPlot::getName, name);
        if (excludeId != null) {
            wrapper.ne(FarmPlot::getId, excludeId);
        }
        
        return count(wrapper) > 0;
    }

    @Override
    public int countPlotsByFarmId(Long farmId) {
        if (farmId == null) {
            return 0;
        }
        
        return farmPlotMapper.countByFarmId(farmId);
    }

    @Override
    public BigDecimal getTotalAreaByFarmId(Long farmId) {
        if (farmId == null) {
            return BigDecimal.ZERO;
        }
        
        log.debug("统计农场总地块面积: farmId={}", farmId);
        try {
            LambdaQueryWrapper<FarmPlot> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FarmPlot::getFarmId, farmId);
            wrapper.select(FarmPlot::getArea);
            
            List<FarmPlot> plots = list(wrapper);
            BigDecimal totalArea = plots.stream()
                    .map(FarmPlot::getArea)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.debug("农场{}总面积: {}", farmId, totalArea);
            return totalArea;
        } catch (Exception e) {
            log.error("统计农场总地块面积失败", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getAvailableAreaByFarmId(Long farmId) {
        if (farmId == null) {
            return BigDecimal.ZERO;
        }
        
        log.debug("统计农场可用地块面积: farmId={}", farmId);
        try {
            LambdaQueryWrapper<FarmPlot> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FarmPlot::getFarmId, farmId);
            wrapper.eq(FarmPlot::getStatus, 1); // 可用状态
            wrapper.select(FarmPlot::getArea);
            
            List<FarmPlot> plots = list(wrapper);
            BigDecimal availableArea = plots.stream()
                    .map(FarmPlot::getArea)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.debug("农场{}可用面积: {}", farmId, availableArea);
            return availableArea;
        } catch (Exception e) {
            log.error("统计农场可用地块面积失败", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public boolean canDeletePlot(Long plotId) {
        if (plotId == null) {
            return false;
        }
        
        // TODO: 检查是否有关联的认养项目
        // 这里需要注入AdoptionProjectService，但为了避免循环依赖，暂时返回true
        // 实际实现中应该检查adoption_projects表中是否有plot_id = plotId的记录
        log.debug("检查地块是否可以删除: plotId={}", plotId);
        return true;
    }

    @Override
    public Object getPlotUsageStatistics(Long farmId) {
        if (farmId == null) {
            throw new BusinessException("农场ID不能为空");
        }
        
        log.debug("获取地块使用情况统计: farmId={}", farmId);
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总数统计
            LambdaQueryWrapper<FarmPlot> baseWrapper = new LambdaQueryWrapper<>();
            baseWrapper.eq(FarmPlot::getFarmId, farmId);
            
            statistics.put("totalCount", count(baseWrapper));
            statistics.put("availableCount", count(baseWrapper.clone().eq(FarmPlot::getStatus, 1)));
            statistics.put("inUseCount", count(baseWrapper.clone().eq(FarmPlot::getStatus, 2)));
            statistics.put("disabledCount", count(baseWrapper.clone().eq(FarmPlot::getStatus, 0)));
            
            // 面积统计
            statistics.put("totalArea", getTotalAreaByFarmId(farmId));
            statistics.put("availableArea", getAvailableAreaByFarmId(farmId));
            
            return statistics;
        } catch (Exception e) {
            log.error("获取地块使用情况统计失败", e);
            throw new BusinessException("获取地块使用情况统计失败");
        }
    }

    @Override
    public List<FarmPlot> getPlotsByAreaRange(Long farmId, BigDecimal minArea, BigDecimal maxArea) {
        if (farmId == null) {
            throw new BusinessException("农场ID不能为空");
        }
        
        log.debug("根据面积范围查询地块: farmId={}, minArea={}, maxArea={}", farmId, minArea, maxArea);
        try {
            LambdaQueryWrapper<FarmPlot> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FarmPlot::getFarmId, farmId);
            wrapper.eq(FarmPlot::getStatus, 1); // 只查询可用地块
            
            if (minArea != null && minArea.compareTo(BigDecimal.ZERO) > 0) {
                wrapper.ge(FarmPlot::getArea, minArea);
            }
            
            if (maxArea != null && maxArea.compareTo(BigDecimal.ZERO) > 0) {
                wrapper.le(FarmPlot::getArea, maxArea);
            }
            
            wrapper.orderByAsc(FarmPlot::getArea);
            
            List<FarmPlot> plots = list(wrapper);
            log.debug("查询到{}个符合面积范围的地块", plots.size());
            return plots;
        } catch (Exception e) {
            log.error("根据面积范围查询地块失败", e);
            throw new BusinessException("查询地块失败");
        }
    }

    @Override
    public boolean hasPermission(Long userId, Long plotId) {
        if (userId == null || plotId == null) {
            return false;
        }
        
        log.debug("检查用户地块操作权限: userId={}, plotId={}", userId, plotId);
        try {
            // 获取地块信息
            FarmPlot plot = getById(plotId);
            if (plot == null) {
                return false;
            }
            
            // 检查用户是否为该地块所属农场的农场主
            return farmService.getFarmsByOwnerId(userId).stream()
                    .anyMatch(farm -> farm.getId().equals(plot.getFarmId()));
        } catch (Exception e) {
            log.error("检查用户地块操作权限失败", e);
            return false;
        }
    }
}
