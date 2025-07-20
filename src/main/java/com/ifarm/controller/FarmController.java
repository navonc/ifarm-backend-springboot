package com.ifarm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.common.result.Result;
import com.ifarm.common.util.BeanUtils;
import com.ifarm.dto.farm.FarmCreateDTO;
import com.ifarm.dto.farm.FarmUpdateDTO;
import com.ifarm.entity.Farm;
import com.ifarm.entity.User;
import com.ifarm.service.IFarmService;
import com.ifarm.service.IUserService;
import com.ifarm.vo.farm.FarmVO;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * 农场管理Controller
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@RestController
@RequestMapping("/api/farms")
@RequiredArgsConstructor
@Tag(name = "农场管理", description = "农场相关接口")
public class FarmController {

    private final IFarmService farmService;
    private final IUserService userService;

    @GetMapping
    @Operation(summary = "获取农场列表", description = "获取农场列表，支持分页和搜索")
    public Result<IPage<FarmVO>> getFarms(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "农场名称") @RequestParam(required = false) String farmName,
            @Parameter(description = "农场主ID") @RequestParam(required = false) Long ownerId,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean enabled) {
        
        log.info("获取农场列表: current={}, size={}, farmName={}, ownerId={}, enabled={}", 
                current, size, farmName, ownerId, enabled);
        
        Page<Farm> page = new Page<>(current, size);
        // 构建查询条件
        LambdaQueryWrapper<Farm> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(farmName)) {
            wrapper.like(Farm::getName, farmName);
        }
        if (ownerId != null) {
            wrapper.eq(Farm::getOwnerId, ownerId);
        }
        if (enabled != null) {
            wrapper.eq(Farm::getStatus, enabled ? 1 : 0);
        }
        wrapper.orderByDesc(Farm::getCreateTime);

        IPage<Farm> farmPage = farmService.page(page, wrapper);
        
        // 转换为VO
        IPage<FarmVO> voPage = farmPage.convert(this::convertToVO);
        
        return Result.success(voPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取农场详情", description = "根据ID获取农场详情")
    public Result<FarmVO> getFarmById(
            @Parameter(description = "农场ID") @PathVariable Long id) {
        
        log.info("获取农场详情: id={}", id);
        
        Farm farm = farmService.getById(id);
        if (farm == null) {
            return Result.error("农场不存在");
        }
        
        FarmVO vo = convertToVO(farm);
        return Result.success(vo);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('FARM_OWNER')")
    @Operation(summary = "获取我的农场", description = "获取当前用户的农场列表")
    public Result<List<FarmVO>> getMyFarms(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("获取我的农场: userId={}", userId);

        LambdaQueryWrapper<Farm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Farm::getOwnerId, userId)
               .eq(Farm::getStatus, 1)
               .orderByDesc(Farm::getCreateTime);

        List<Farm> farms = farmService.list(wrapper);
        List<FarmVO> voList = farms.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(voList);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_OWNER')")
    @Operation(summary = "创建农场", description = "创建新的农场")
    public Result<FarmVO> createFarm(@Valid @RequestBody FarmCreateDTO createDTO) {
        log.info("创建农场: {}", createDTO);

        Farm farm = BeanUtils.copyProperties(createDTO, Farm.class);
        if (farm != null) {
            farm.setName(createDTO.getFarmName());
            farm.setStatus(createDTO.getEnabled() ? 1 : 0);
        }
        boolean result = farmService.save(farm);

        if (result) {
            FarmVO vo = convertToVO(farm);
            return Result.success(vo);
        } else {
            return Result.error("创建农场失败");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @farmService.hasPermission(authentication.name, #id)")
    @Operation(summary = "更新农场", description = "更新农场信息")
    public Result<FarmVO> updateFarm(
            @Parameter(description = "农场ID") @PathVariable Long id,
            @Valid @RequestBody FarmUpdateDTO updateDTO,
            Authentication authentication) {
        
        log.info("更新农场: id={}, updateDTO={}, user={}", id, updateDTO, authentication.getName());
        
        Farm farm = BeanUtils.copyProperties(updateDTO, Farm.class);
        farm.setId(id);
        boolean result = farmService.updateFarm(farm);
        
        if (result) {
            Farm updatedFarm = farmService.getById(id);
            FarmVO vo = convertToVO(updatedFarm);
            return Result.success(vo);
        } else {
            return Result.error("更新农场失败");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除农场", description = "删除农场（仅管理员）")
    public Result<Void> deleteFarm(
            @Parameter(description = "农场ID") @PathVariable Long id) {
        
        log.info("删除农场: id={}", id);
        
        boolean result = farmService.deleteFarm(id);
        
        if (result) {
            return Result.success();
        } else {
            return Result.error("删除农场失败");
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新农场状态", description = "启用或禁用农场（仅管理员）")
    public Result<Void> updateFarmStatus(
            @Parameter(description = "农场ID") @PathVariable Long id,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled) {

        log.info("更新农场状态: id={}, enabled={}", id, enabled);

        Farm farm = new Farm();
        farm.setId(id);
        farm.setStatus(enabled ? 1 : 0);
        boolean result = farmService.updateById(farm);

        if (result) {
            return Result.success();
        } else {
            return Result.error("更新农场状态失败");
        }
    }

    @GetMapping("/search")
    @Operation(summary = "搜索农场", description = "根据关键词搜索农场")
    public Result<List<FarmVO>> searchFarms(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit) {

        log.info("搜索农场: keyword={}, limit={}", keyword, limit);

        LambdaQueryWrapper<Farm> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Farm::getName, keyword)
               .or()
               .like(Farm::getDescription, keyword)
               .eq(Farm::getStatus, 1)
               .orderByDesc(Farm::getCreateTime)
               .last("LIMIT " + limit);

        List<Farm> farms = farmService.list(wrapper);
        List<FarmVO> voList = farms.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(voList);
    }

    /**
     * 转换为VO对象
     */
    private FarmVO convertToVO(Farm farm) {
        FarmVO vo = BeanUtils.copyProperties(farm, FarmVO.class);
        if (vo != null) {
            vo.setFarmName(farm.getName());
            vo.setEnabled(farm.getStatus() == 1);

            // 设置农场主姓名
            if (farm.getOwnerId() != null) {
                User owner = userService.getById(farm.getOwnerId());
                if (owner != null) {
                    vo.setOwnerName(owner.getNickname() != null ? owner.getNickname() : owner.getUsername());
                }
            }

            // 设置统计数据（暂时设为0，需要相关Service支持）
            vo.setPlotCount(0);
            vo.setProjectCount(0);
            vo.setTotalAdoptionCount(0);
            vo.setRating(BigDecimal.ZERO);
            vo.setReviewCount(0);
        }

        return vo;
    }
}
