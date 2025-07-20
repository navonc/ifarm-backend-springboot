package com.ifarm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.common.result.Result;
import com.ifarm.common.util.BeanUtils;
import com.ifarm.dto.category.CategoryCreateDTO;
import com.ifarm.dto.category.CategoryUpdateDTO;
import com.ifarm.entity.Category;
import com.ifarm.service.ICategoryService;
import com.ifarm.vo.category.CategoryVO;
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
 * 分类管理Controller
 *
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "分类相关接口")
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping
    @Operation(summary = "获取分类列表", description = "获取分类列表，支持分页和搜索")
    public Result<IPage<CategoryVO>> getCategories(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "分类名称") @RequestParam(required = false) String categoryName,
            @Parameter(description = "父分类ID") @RequestParam(required = false) Long parentId,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean enabled) {

        log.info("获取分类列表: current={}, size={}, categoryName={}, parentId={}, enabled={}",
                current, size, categoryName, parentId, enabled);

        Page<Category> page = new Page<>(current, size);
        // 构建查询条件
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(categoryName)) {
            wrapper.like(Category::getName, categoryName);
        }
        if (parentId != null) {
            wrapper.eq(Category::getParentId, parentId);
        }
        if (enabled != null) {
            wrapper.eq(Category::getStatus, enabled ? 1 : 0);
        }
        wrapper.orderByAsc(Category::getSortOrder, Category::getId);

        IPage<Category> categoryPage = categoryService.page(page, wrapper);

        // 转换为VO
        IPage<CategoryVO> voPage = categoryPage.convert(category -> {
            CategoryVO vo = BeanUtils.copyProperties(category, CategoryVO.class);
            if (vo != null) {
                vo.setCategoryName(category.getName());
            }
            if (vo != null) {
                vo.setEnabled(category.getStatus() == 1);
            }
            return vo;
        });

        return Result.success(voPage);
    }

    @GetMapping("/tree")
    @Operation(summary = "获取分类树", description = "获取完整的分类树结构")
    public Result<List<CategoryVO>> getCategoryTree() {
        log.info("获取分类树");

        List<Category> categories = categoryService.list();
        List<CategoryVO> voList = categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(voList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情", description = "根据ID获取分类详情")
    public Result<CategoryVO> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        
        log.info("获取分类详情: id={}", id);
        
        Category category = categoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        
        CategoryVO vo = convertToVO(category);
        return Result.success(vo);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_OWNER')")
    @Operation(summary = "创建分类", description = "创建新的分类")
    public Result<CategoryVO> createCategory(@Valid @RequestBody CategoryCreateDTO createDTO) {
        log.info("创建分类: {}", createDTO);

        Category category = BeanUtils.copyProperties(createDTO, Category.class);
        if (category != null) {
            category.setName(createDTO.getCategoryName());
        }
        if (category != null) {
            category.setStatus(createDTO.getEnabled() ? 1 : 0);
        }
        boolean result = categoryService.save(category);

        if (result) {
            CategoryVO vo = convertToVO(category);
            return Result.success(vo);
        } else {
            return Result.error("创建分类失败");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_OWNER')")
    @Operation(summary = "更新分类", description = "更新分类信息")
    public Result<CategoryVO> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateDTO updateDTO) {
        
        log.info("更新分类: id={}, updateDTO={}", id, updateDTO);
        
        Category category = BeanUtils.copyProperties(updateDTO, Category.class);
        if (category != null) {
            category.setId(id);
            if (updateDTO.getCategoryName() != null) {
                category.setName(updateDTO.getCategoryName());
            }
            if (updateDTO.getEnabled() != null) {
                category.setStatus(updateDTO.getEnabled() ? 1 : 0);
            }
        }
        boolean result = categoryService.updateById(category);
        
        if (result) {
            Category updatedCategory = categoryService.getById(id);
            CategoryVO vo = convertToVO(updatedCategory);
            return Result.success(vo);
        } else {
            return Result.error("更新分类失败");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除分类", description = "删除分类（仅管理员）")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {

        log.info("删除分类: id={}", id);

        boolean result = categoryService.removeById(id);

        if (result) {
            return Result.success();
        } else {
            return Result.error("删除分类失败");
        }
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "获取子分类", description = "获取指定分类的子分类列表")
    public Result<List<CategoryVO>> getChildrenCategories(
            @Parameter(description = "父分类ID") @PathVariable Long id) {

        log.info("获取子分类: parentId={}", id);

        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, id);
        List<Category> children = categoryService.list(wrapper);
        List<CategoryVO> voList = children.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(voList);
    }

    @PostMapping("/batch-delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "批量删除分类", description = "批量删除分类（仅管理员）")
    public Result<Void> batchDeleteCategories(
            @Parameter(description = "分类ID列表") @RequestBody List<Long> ids) {

        log.info("批量删除分类: ids={}", ids);

        boolean result = categoryService.removeByIds(ids);

        if (result) {
            return Result.success();
        } else {
            return Result.error("批量删除分类失败");
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_OWNER')")
    @Operation(summary = "更新分类状态", description = "启用或禁用分类")
    public Result<Void> updateCategoryStatus(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled) {

        log.info("更新分类状态: id={}, enabled={}", id, enabled);

        Category category = new Category();
        category.setId(id);
        category.setStatus(enabled ? 1 : 0);
        boolean result = categoryService.updateById(category);

        if (result) {
            return Result.success();
        } else {
            return Result.error("更新分类状态失败");
        }
    }

    /**
     * 转换为VO对象
     */
    private CategoryVO convertToVO(Category category) {
        CategoryVO vo = BeanUtils.copyProperties(category, CategoryVO.class);
        if (vo != null) {
            vo.setCategoryName(category.getName());
            vo.setEnabled(category.getStatus() == 1);

            // 设置父分类名称
            if (category.getParentId() != null && category.getParentId() > 0) {
                Category parent = categoryService.getById(category.getParentId());
                if (parent != null) {
                    vo.setParentName(parent.getName());
                }
            }

            // 设置子分类数量
            LambdaQueryWrapper<Category> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(Category::getParentId, category.getId());
            vo.setChildrenCount((int) categoryService.count(childWrapper));

            // 设置作物数量（暂时设为0，需要CropService支持）
            vo.setCropCount(0);
        }

        return vo;
    }
}
