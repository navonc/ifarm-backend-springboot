package com.ifarm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.common.result.Result;
import com.ifarm.common.util.BeanUtils;
import com.ifarm.dto.farmplot.FarmPlotCreateDTO;
import com.ifarm.dto.farmplot.FarmPlotUpdateDTO;
import com.ifarm.entity.Farm;
import com.ifarm.entity.FarmPlot;
import com.ifarm.service.IFarmPlotService;
import com.ifarm.service.IFarmService;
import com.ifarm.vo.farmplot.FarmPlotVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 地块管理Controller
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@RestController
@RequestMapping("/api/farm-plots")
@RequiredArgsConstructor
@Tag(name = "地块管理", description = "地块相关接口")
public class FarmPlotController {

    private final IFarmPlotService farmPlotService;
    private final IFarmService farmService;

    @GetMapping
    @Operation(summary = "获取地块列表", description = "获取地块列表，支持分页和搜索")
    public Result<IPage<FarmPlotVO>> getFarmPlots(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "地块名称") @RequestParam(required = false) String plotName,
            @Parameter(description = "农场ID") @RequestParam(required = false) Long farmId,
            @Parameter(description = "地块状态") @RequestParam(required = false) Integer plotStatus,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean enabled) {
        
        log.info("获取地块列表: current={}, size={}, plotName={}, farmId={}, plotStatus={}, enabled={}", 
                current, size, plotName, farmId, plotStatus, enabled);
        
        Page<FarmPlot> page = new Page<>(current, size);
        // 构建查询条件
        LambdaQueryWrapper<FarmPlot> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(plotName)) {
            wrapper.like(FarmPlot::getName, plotName);
        }
        if (farmId != null) {
            wrapper.eq(FarmPlot::getFarmId, farmId);
        }
        if (plotStatus != null) {
            wrapper.eq(FarmPlot::getStatus, plotStatus);
        }
        if (enabled != null) {
            wrapper.eq(FarmPlot::getStatus, enabled ? 1 : 0);
        }
        wrapper.orderByDesc(FarmPlot::getCreateTime);

        IPage<FarmPlot> plotPage = farmPlotService.page(page, wrapper);
        
        // 转换为VO
        IPage<FarmPlotVO> voPage = plotPage.convert(this::convertToVO);
        
        return Result.success(voPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取地块详情", description = "根据ID获取地块详情")
    public Result<FarmPlotVO> getFarmPlotById(
            @Parameter(description = "地块ID") @PathVariable Long id) {
        
        log.info("获取地块详情: id={}", id);
        
        FarmPlot plot = farmPlotService.getById(id);
        if (plot == null) {
            return Result.error("地块不存在");
        }
        
        FarmPlotVO vo = convertToVO(plot);
        return Result.success(vo);
    }

    @GetMapping("/farm/{farmId}")
    @Operation(summary = "获取农场地块", description = "获取指定农场的所有地块")
    public Result<List<FarmPlotVO>> getPlotsByFarmId(
            @Parameter(description = "农场ID") @PathVariable Long farmId) {
        
        log.info("获取农场地块: farmId={}", farmId);
        
        List<FarmPlot> plots = farmPlotService.getPlotsByFarmId(farmId);
        List<FarmPlotVO> voList = plots.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return Result.success(voList);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @farmService.hasPermission(authentication.name, #createDTO.farmId)")
    @Operation(summary = "创建地块", description = "创建新的地块")
    public Result<FarmPlotVO> createFarmPlot(
            @Valid @RequestBody FarmPlotCreateDTO createDTO,
            Authentication authentication) {
        
        log.info("创建地块: {}, user={}", createDTO, authentication.getName());
        
        FarmPlot plot = BeanUtils.copyProperties(createDTO, FarmPlot.class);
        boolean result = farmPlotService.createPlot(plot);
        
        if (result) {
            FarmPlotVO vo = convertToVO(plot);
            return Result.success(vo);
        } else {
            return Result.error("创建地块失败");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @farmPlotService.hasPermission(authentication.name, #id)")
    @Operation(summary = "更新地块", description = "更新地块信息")
    public Result<FarmPlotVO> updateFarmPlot(
            @Parameter(description = "地块ID") @PathVariable Long id,
            @Valid @RequestBody FarmPlotUpdateDTO updateDTO,
            Authentication authentication) {
        
        log.info("更新地块: id={}, updateDTO={}, user={}", id, updateDTO, authentication.getName());
        
        FarmPlot plot = BeanUtils.copyProperties(updateDTO, FarmPlot.class);
        if (plot != null) {
            plot.setId(id);
        }
        boolean result = farmPlotService.updatePlot(plot);
        
        if (result) {
            FarmPlot updatedPlot = farmPlotService.getById(id);
            FarmPlotVO vo = convertToVO(updatedPlot);
            return Result.success(vo);
        } else {
            return Result.error("更新地块失败");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @farmPlotService.hasPermission(authentication.name, #id)")
    @Operation(summary = "删除地块", description = "删除地块")
    public Result<Void> deleteFarmPlot(
            @Parameter(description = "地块ID") @PathVariable Long id,
            Authentication authentication) {
        
        log.info("删除地块: id={}, user={}", id, authentication.getName());
        
        boolean result = farmPlotService.deletePlot(id);
        
        if (result) {
            return Result.success();
        } else {
            return Result.error("删除地块失败");
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @farmPlotService.hasPermission(authentication.name, #id)")
    @Operation(summary = "更新地块状态", description = "更新地块状态")
    public Result<Void> updatePlotStatus(
            @Parameter(description = "地块ID") @PathVariable Long id,
            @Parameter(description = "地块状态") @RequestParam Integer plotStatus,
            Authentication authentication) {
        
        log.info("更新地块状态: id={}, plotStatus={}, user={}", id, plotStatus, authentication.getName());
        
        boolean result = farmPlotService.updatePlotStatus(id, plotStatus);
        
        if (result) {
            return Result.success();
        } else {
            return Result.error("更新地块状态失败");
        }
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_OWNER')")
    @Operation(summary = "启用/禁用地块", description = "启用或禁用地块")
    public Result<Void> updatePlotEnabled(
            @Parameter(description = "地块ID") @PathVariable Long id,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled,
            Authentication authentication) {

        log.info("更新地块启用状态: id={}, enabled={}, user={}", id, enabled, authentication.getName());

        FarmPlot plot = new FarmPlot();
        plot.setId(id);
        plot.setStatus(enabled ? 1 : 0);
        boolean result = farmPlotService.updateById(plot);

        if (result) {
            return Result.success();
        } else {
            return Result.error("更新地块启用状态失败");
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态获取地块", description = "根据地块状态获取地块列表")
    public Result<List<FarmPlotVO>> getPlotsByStatus(
            @Parameter(description = "地块状态") @PathVariable Integer status) {

        log.info("根据状态获取地块: status={}", status);

        LambdaQueryWrapper<FarmPlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmPlot::getStatus, status)
               .orderByDesc(FarmPlot::getCreateTime);

        List<FarmPlot> plots = farmPlotService.list(wrapper);
        List<FarmPlotVO> voList = plots.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(voList);
    }

    /**
     * 转换为VO对象
     */
    private FarmPlotVO convertToVO(FarmPlot plot) {
        FarmPlotVO vo = BeanUtils.copyProperties(plot, FarmPlotVO.class);
        
        // 设置农场名称
        if (plot.getFarmId() != null) {
            Farm farm = farmService.getById(plot.getFarmId());
            if (farm != null) {
                if (vo != null) {
                    vo.setFarmName(farm.getName());
                }
            }
        }
        
        // 设置地块状态名称
        if (vo != null) {
            vo.setPlotName(plot.getName());
            vo.setEnabled(plot.getStatus() == 1);
            vo.setPlotStatus(plot.getStatus());
            vo.setPlotStatusName(getPlotStatusName(plot.getStatus()));

            // 设置统计数据（暂时设为0，需要相关Service支持）
            vo.setProjectCount(0);
            vo.setUnitCount(0);
            vo.setAdoptedUnitCount(0);
            vo.setAdoptionRate(BigDecimal.ZERO);
            vo.setCurrentCrop("");
        }

        return vo;
    }

    /**
     * 获取地块状态名称
     */
    private String getPlotStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        
        Map<Integer, String> statusMap = new HashMap<>();
        statusMap.put(1, "空闲");
        statusMap.put(2, "种植中");
        statusMap.put(3, "收获中");
        statusMap.put(4, "休耕");
        
        return statusMap.getOrDefault(status, "未知");
    }
}
