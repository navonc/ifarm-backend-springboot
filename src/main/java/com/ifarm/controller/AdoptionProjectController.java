package com.ifarm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ifarm.common.result.Result;
import com.ifarm.dto.adoption.AdoptionProjectCreateDTO;
import com.ifarm.dto.adoption.AdoptionProjectQueryDTO;
import com.ifarm.dto.adoption.AdoptionProjectUpdateDTO;
import com.ifarm.service.IAdoptionProjectService;
import com.ifarm.vo.adoption.AdoptionProjectVO;
import com.ifarm.vo.adoption.ProjectUnitVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认养项目管理控制器
 * 
 * @author ifarm
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/adoption-projects")
@RequiredArgsConstructor
@Tag(name = "认养项目管理", description = "认养项目的创建、查询、更新等操作")
public class AdoptionProjectController {

    private final IAdoptionProjectService adoptionProjectService;

    /**
     * 获取认养项目列表
     */
    @GetMapping
    @Operation(summary = "获取认养项目列表", description = "分页查询认养项目列表，支持多条件筛选")
    public Result<IPage<AdoptionProjectVO>> getAdoptionProjects(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "查询条件") @ModelAttribute AdoptionProjectQueryDTO queryDTO) {
        
        log.info("获取认养项目列表: current={}, size={}, queryDTO={}", current, size, queryDTO);
        IPage<AdoptionProjectVO> result = adoptionProjectService.getAdoptionProjects(current, size, queryDTO);
        return Result.success(result);
    }

    /**
     * 获取认养项目详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取认养项目详情", description = "根据项目ID获取认养项目的详细信息")
    public Result<AdoptionProjectVO> getAdoptionProjectById(
            @Parameter(description = "项目ID", example = "1") @PathVariable Long id) {
        
        log.info("获取认养项目详情: id={}", id);
        AdoptionProjectVO result = adoptionProjectService.getAdoptionProjectById(id);
        return Result.success(result);
    }

    /**
     * 创建认养项目
     */
    @PostMapping
    @Operation(summary = "创建认养项目", description = "创建新的认养项目（需要管理员或农场主权限）")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_OWNER')")
    public Result<AdoptionProjectVO> createAdoptionProject(
            @Parameter(description = "认养项目创建信息") @Valid @RequestBody AdoptionProjectCreateDTO createDTO,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        log.info("创建认养项目: userId={}, createDTO={}", userId, createDTO);
        
        AdoptionProjectVO result = adoptionProjectService.createAdoptionProject(createDTO, userId);
        return Result.success(result);
    }

    /**
     * 更新认养项目
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新认养项目", description = "更新认养项目信息（需要管理员或农场主权限）")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_OWNER')")
    public Result<AdoptionProjectVO> updateAdoptionProject(
            @Parameter(description = "项目ID", example = "1") @PathVariable Long id,
            @Parameter(description = "认养项目更新信息") @Valid @RequestBody AdoptionProjectUpdateDTO updateDTO,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        log.info("更新认养项目: id={}, userId={}, updateDTO={}", id, userId, updateDTO);
        
        AdoptionProjectVO result = adoptionProjectService.updateAdoptionProject(id, updateDTO, userId);
        return Result.success(result);
    }

    /**
     * 删除认养项目
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除认养项目", description = "删除认养项目（需要管理员权限）")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteAdoptionProject(
            @Parameter(description = "项目ID", example = "1") @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        log.info("删除认养项目: id={}, userId={}", id, userId);
        
        adoptionProjectService.deleteAdoptionProject(id, userId);
        return Result.success();
    }

    /**
     * 获取项目单元列表
     */
    @GetMapping("/{id}/units")
    @Operation(summary = "获取项目单元列表", description = "获取指定认养项目的所有单元信息")
    public Result<List<ProjectUnitVO>> getProjectUnits(
            @Parameter(description = "项目ID", example = "1") @PathVariable Long id,
            @Parameter(description = "单元状态筛选", example = "1") @RequestParam(required = false) Integer unitStatus) {
        
        log.info("获取项目单元列表: projectId={}, unitStatus={}", id, unitStatus);
        List<ProjectUnitVO> result = adoptionProjectService.getProjectUnits(id, unitStatus);
        return Result.success(result);
    }

    /**
     * 获取热门认养项目
     */
    @GetMapping("/popular")
    @Operation(summary = "获取热门认养项目", description = "获取热门认养项目列表，按认养率排序")
    public Result<List<AdoptionProjectVO>> getPopularProjects(
            @Parameter(description = "返回数量", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        
        log.info("获取热门认养项目: limit={}", limit);
        List<AdoptionProjectVO> result = adoptionProjectService.getPopularProjectsVO(limit);
        return Result.success(result);
    }

    /**
     * 搜索认养项目
     */
    @GetMapping("/search")
    @Operation(summary = "搜索认养项目", description = "根据关键词搜索认养项目")
    public Result<IPage<AdoptionProjectVO>> searchAdoptionProjects(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索条件") @ModelAttribute AdoptionProjectQueryDTO queryDTO) {

        log.info("搜索认养项目: current={}, size={}, queryDTO={}", current, size, queryDTO);
        IPage<AdoptionProjectVO> result = adoptionProjectService.searchAdoptionProjects(current, size, queryDTO);
        return Result.success(result);
    }
}
