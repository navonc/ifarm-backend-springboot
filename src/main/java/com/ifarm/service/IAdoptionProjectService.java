package com.ifarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.AdoptionProject;

import java.util.List;

/**
 * 认养项目服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IAdoptionProjectService extends IService<AdoptionProject> {

    /**
     * 根据地块ID查询项目列表
     * 
     * @param plotId 地块ID
     * @return 项目列表
     */
    List<AdoptionProject> getProjectsByPlotId(Long plotId);

    /**
     * 根据作物ID查询项目列表
     * 
     * @param cropId 作物ID
     * @return 项目列表
     */
    List<AdoptionProject> getProjectsByCropId(Long cropId);

    /**
     * 查询可认养的项目列表
     * 
     * @return 可认养的项目列表
     */
    List<AdoptionProject> getAvailableProjects();

    /**
     * 分页查询认养项目列表
     * 
     * @param page 分页参数
     * @param cropId 作物ID（可选）
     * @param projectStatus 项目状态（可选）
     * @param name 项目名称（可选，模糊查询）
     * @return 分页结果
     */
    IPage<AdoptionProject> getProjectPage(Page<AdoptionProject> page, Long cropId, 
                                         Integer projectStatus, String name);

    /**
     * 根据项目状态查询项目列表
     * 
     * @param projectStatus 项目状态
     * @return 项目列表
     */
    List<AdoptionProject> getProjectsByStatus(Integer projectStatus);

    /**
     * 创建认养项目
     * 
     * @param adoptionProject 项目信息
     * @return 创建结果
     */
    boolean createProject(AdoptionProject adoptionProject);

    /**
     * 更新项目信息
     * 
     * @param adoptionProject 项目信息
     * @return 更新结果
     */
    boolean updateProject(AdoptionProject adoptionProject);

    /**
     * 删除项目
     * 
     * @param projectId 项目ID
     * @return 删除结果
     */
    boolean deleteProject(Long projectId);

    /**
     * 更新项目状态
     * 
     * @param projectId 项目ID
     * @param projectStatus 项目状态
     * @return 操作结果
     */
    boolean updateProjectStatus(Long projectId, Integer projectStatus);

    /**
     * 更新项目可用单元数
     * 
     * @param projectId 项目ID
     * @param unitCount 单元数量（正数增加，负数减少）
     * @return 更新结果
     */
    boolean updateAvailableUnits(Long projectId, Integer unitCount);

    /**
     * 获取项目详情（包含农场和作物信息）
     * 
     * @param projectId 项目ID
     * @return 项目详情
     */
    AdoptionProject getProjectDetail(Long projectId);

    /**
     * 检查项目名称是否存在
     * 
     * @param name 项目名称
     * @param excludeId 排除的项目ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByName(String name, Long excludeId);

    /**
     * 检查项目是否可以认养
     * 
     * @param projectId 项目ID
     * @param unitCount 需要认养的单元数
     * @return 是否可以认养
     */
    boolean canAdopt(Long projectId, Integer unitCount);

    /**
     * 获取热门项目列表
     * 
     * @param limit 限制数量
     * @return 热门项目列表
     */
    List<AdoptionProject> getPopularProjects(Integer limit);

    /**
     * 获取推荐项目列表
     * 
     * @param userId 用户ID（可选，用于个性化推荐）
     * @param limit 限制数量
     * @return 推荐项目列表
     */
    List<AdoptionProject> getRecommendedProjects(Long userId, Integer limit);

    /**
     * 搜索项目（支持名称、描述模糊搜索）
     * 
     * @param keyword 搜索关键词
     * @param page 分页参数
     * @return 搜索结果
     */
    IPage<AdoptionProject> searchProjects(String keyword, Page<AdoptionProject> page);

    /**
     * 获取项目统计信息
     * 
     * @param farmId 农场ID（可选）
     * @return 统计信息
     */
    Object getProjectStatistics(Long farmId);

    /**
     * 检查用户是否有权限操作项目
     * 
     * @param userId 用户ID
     * @param projectId 项目ID
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, Long projectId);

    /**
     * 开始项目种植
     * 
     * @param projectId 项目ID
     * @return 操作结果
     */
    boolean startPlanting(Long projectId);

    /**
     * 开始项目收获
     * 
     * @param projectId 项目ID
     * @return 操作结果
     */
    boolean startHarvesting(Long projectId);

    /**
     * 完成项目
     * 
     * @param projectId 项目ID
     * @return 操作结果
     */
    boolean completeProject(Long projectId);
}
