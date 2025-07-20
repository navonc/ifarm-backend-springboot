package com.ifarm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.common.result.Result;
import com.ifarm.common.util.BeanUtils;
import com.ifarm.dto.crop.CropCreateDTO;
import com.ifarm.dto.crop.CropUpdateDTO;
import com.ifarm.entity.Category;
import com.ifarm.entity.Crop;
import com.ifarm.service.ICategoryService;
import com.ifarm.service.ICropService;
import com.ifarm.vo.crop.CropVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 作物管理Controller
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
@Tag(name = "作物管理", description = "作物相关接口")
public class CropController {

    private final ICropService cropService;
    private final ICategoryService categoryService;

    @GetMapping
    @Operation(summary = "获取作物列表", description = "获取作物列表，支持分页和搜索")
    public Result<IPage<CropVO>> getCrops(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "作物名称") @RequestParam(required = false) String cropName,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean enabled) {
        
        log.info("获取作物列表: current={}, size={}, cropName={}, categoryId={}, enabled={}", 
                current, size, cropName, categoryId, enabled);
        
        Page<Crop> page = new Page<>(current, size);
        // 构建查询条件
        LambdaQueryWrapper<Crop> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(cropName)) {
            wrapper.like(Crop::getName, cropName);
        }
        if (categoryId != null) {
            wrapper.eq(Crop::getCategoryId, categoryId);
        }
        if (enabled != null) {
            wrapper.eq(Crop::getStatus, enabled ? 1 : 0);
        }
        wrapper.orderByDesc(Crop::getCreateTime);

        IPage<Crop> cropPage = cropService.page(page, wrapper);
        
        // 转换为VO
        IPage<CropVO> voPage = cropPage.convert(this::convertToVO);
        
        return Result.success(voPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取作物详情", description = "根据ID获取作物详情")
    public Result<CropVO> getCropById(
            @Parameter(description = "作物ID") @PathVariable Long id) {
        
        log.info("获取作物详情: id={}", id);
        
        Crop crop = cropService.getById(id);
        if (crop == null) {
            return Result.error("作物不存在");
        }
        
        CropVO vo = convertToVO(crop);
        return Result.success(vo);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类获取作物", description = "根据分类ID获取作物列表")
    public Result<List<CropVO>> getCropsByCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        
        log.info("根据分类获取作物: categoryId={}", categoryId);
        
        List<Crop> crops = cropService.getCropsByCategoryId(categoryId);
        List<CropVO> voList = crops.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return Result.success(voList);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_OWNER')")
    @Operation(summary = "创建作物", description = "创建新的作物")
    public Result<CropVO> createCrop(@Valid @RequestBody CropCreateDTO createDTO) {
        log.info("创建作物: {}", createDTO);
        
        Crop crop = BeanUtils.copyProperties(createDTO, Crop.class);
        boolean result = cropService.createCrop(crop);
        
        if (result) {
            CropVO vo = convertToVO(crop);
            return Result.success(vo);
        } else {
            return Result.error("创建作物失败");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_OWNER')")
    @Operation(summary = "更新作物", description = "更新作物信息")
    public Result<CropVO> updateCrop(
            @Parameter(description = "作物ID") @PathVariable Long id,
            @Valid @RequestBody CropUpdateDTO updateDTO) {
        
        log.info("更新作物: id={}, updateDTO={}", id, updateDTO);
        
        Crop crop = BeanUtils.copyProperties(updateDTO, Crop.class);
        if (crop != null) {
            crop.setId(id);
        }
        boolean result = cropService.updateCrop(crop);
        
        if (result) {
            Crop updatedCrop = cropService.getById(id);
            CropVO vo = convertToVO(updatedCrop);
            return Result.success(vo);
        } else {
            return Result.error("更新作物失败");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除作物", description = "删除作物（仅管理员）")
    public Result<Void> deleteCrop(
            @Parameter(description = "作物ID") @PathVariable Long id) {
        
        log.info("删除作物: id={}", id);
        
        boolean result = cropService.deleteCrop(id);
        
        if (result) {
            return Result.success();
        } else {
            return Result.error("删除作物失败");
        }
    }

    @PostMapping("/batch-delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "批量删除作物", description = "批量删除作物（仅管理员）")
    public Result<Void> batchDeleteCrops(
            @Parameter(description = "作物ID列表") @RequestBody List<Long> ids) {

        log.info("批量删除作物: ids={}", ids);

        boolean result = cropService.removeByIds(ids);

        if (result) {
            return Result.success();
        } else {
            return Result.error("批量删除作物失败");
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_OWNER')")
    @Operation(summary = "更新作物状态", description = "启用或禁用作物")
    public Result<Void> updateCropStatus(
            @Parameter(description = "作物ID") @PathVariable Long id,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled) {

        log.info("更新作物状态: id={}, enabled={}", id, enabled);

        Crop crop = new Crop();
        crop.setId(id);
        crop.setStatus(enabled ? 1 : 0);
        boolean result = cropService.updateById(crop);

        if (result) {
            return Result.success();
        } else {
            return Result.error("更新作物状态失败");
        }
    }

    @GetMapping("/search")
    @Operation(summary = "搜索作物", description = "根据关键词搜索作物")
    public Result<List<CropVO>> searchCrops(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit) {

        log.info("搜索作物: keyword={}, limit={}", keyword, limit);

        LambdaQueryWrapper<Crop> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Crop::getName, keyword)
               .or()
               .like(Crop::getVariety, keyword)
               .orderByDesc(Crop::getCreateTime)
               .last("LIMIT " + limit);

        List<Crop> crops = cropService.list(wrapper);
        List<CropVO> voList = crops.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(voList);
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门作物", description = "获取热门作物列表")
    public Result<List<CropVO>> getPopularCrops(
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        
        log.info("获取热门作物: limit={}", limit);
        
        List<Crop> crops = cropService.getPopularCrops(limit);
        List<CropVO> voList = crops.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return Result.success(voList);
    }

    @GetMapping("/seasons/{season}")
    @Operation(summary = "根据季节获取作物", description = "根据种植季节获取作物列表")
    public Result<List<CropVO>> getCropsBySeason(
            @Parameter(description = "季节") @PathVariable String season) {

        log.info("根据季节获取作物: season={}", season);

        LambdaQueryWrapper<Crop> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Crop::getPlantingSeason, season)
               .eq(Crop::getStatus, 1)
               .orderByDesc(Crop::getCreateTime);

        List<Crop> crops = cropService.list(wrapper);
        List<CropVO> voList = crops.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(voList);
    }

    /**
     * 转换为VO对象
     */
    private CropVO convertToVO(Crop crop) {
        CropVO vo = BeanUtils.copyProperties(crop, CropVO.class);
        if (vo != null) {
            vo.setCropName(crop.getName());
            vo.setEnabled(crop.getStatus() == 1);

            // 设置分类名称
            if (crop.getCategoryId() != null) {
                Category category = categoryService.getById(crop.getCategoryId());
                if (category != null) {
                    vo.setCategoryName(category.getName());
                }
            }

            // 设置项目数量（暂时设为0，需要ProjectService支持）
            vo.setProjectCount(0);

            // 设置总认养数量（暂时设为0，需要AdoptionService支持）
            vo.setTotalAdoptionCount(0);
        }

        return vo;
    }
}
