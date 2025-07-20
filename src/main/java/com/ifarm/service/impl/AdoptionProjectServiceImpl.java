package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.dto.adoption.AdoptionProjectCreateDTO;
import com.ifarm.dto.adoption.AdoptionProjectQueryDTO;
import com.ifarm.dto.adoption.AdoptionProjectUpdateDTO;
import com.ifarm.entity.AdoptionProject;
import com.ifarm.entity.ProjectUnit;
import com.ifarm.vo.adoption.AdoptionProjectVO;
import com.ifarm.vo.adoption.ProjectUnitVO;
import com.ifarm.mapper.AdoptionProjectMapper;
import com.ifarm.service.IAdoptionProjectService;
import com.ifarm.service.ICropService;
import com.ifarm.service.IFarmPlotService;
import com.ifarm.service.IProjectUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认养项目服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdoptionProjectServiceImpl extends ServiceImpl<AdoptionProjectMapper, AdoptionProject> implements IAdoptionProjectService {

    private final AdoptionProjectMapper adoptionProjectMapper;
    private final IFarmPlotService farmPlotService;
    private final ICropService cropService;
    private final IProjectUnitService projectUnitService;

    @Override
    public List<AdoptionProject> getProjectsByPlotId(Long plotId) {
        if (plotId == null) {
            throw new BusinessException("地块ID不能为空");
        }
        
        log.debug("根据地块ID查询项目列表: {}", plotId);
        try {
            List<AdoptionProject> projects = adoptionProjectMapper.selectByPlotId(plotId);
            log.debug("查询到{}个项目", projects.size());
            return projects;
        } catch (Exception e) {
            log.error("根据地块ID查询项目列表失败，地块ID: {}", plotId, e);
            throw new BusinessException("查询项目列表失败");
        }
    }

    @Override
    public List<AdoptionProject> getProjectsByCropId(Long cropId) {
        if (cropId == null) {
            throw new BusinessException("作物ID不能为空");
        }
        
        log.debug("根据作物ID查询项目列表: {}", cropId);
        try {
            List<AdoptionProject> projects = adoptionProjectMapper.selectByCropId(cropId);
            log.debug("查询到{}个项目", projects.size());
            return projects;
        } catch (Exception e) {
            log.error("根据作物ID查询项目列表失败，作物ID: {}", cropId, e);
            throw new BusinessException("查询项目列表失败");
        }
    }

    @Override
    public List<AdoptionProject> getAvailableProjects() {
        log.debug("查询可认养的项目列表");
        try {
            List<AdoptionProject> projects = adoptionProjectMapper.selectAvailableProjects();
            log.debug("查询到{}个可认养项目", projects.size());
            return projects;
        } catch (Exception e) {
            log.error("查询可认养项目列表失败", e);
            throw new BusinessException("查询项目列表失败");
        }
    }

    @Override
    public IPage<AdoptionProject> getProjectPage(Page<AdoptionProject> page, Long cropId, 
                                                Integer projectStatus, String name) {
        log.debug("分页查询认养项目列表: cropId={}, projectStatus={}, name={}", cropId, projectStatus, name);
        try {
            IPage<AdoptionProject> result = adoptionProjectMapper.selectProjectPage(page, cropId, projectStatus, name);
            log.debug("查询到{}条项目记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("分页查询认养项目列表失败", e);
            throw new BusinessException("查询项目列表失败");
        }
    }

    @Override
    public List<AdoptionProject> getProjectsByStatus(Integer projectStatus) {
        if (projectStatus == null) {
            throw new BusinessException("项目状态不能为空");
        }
        
        log.debug("根据项目状态查询项目列表: {}", projectStatus);
        try {
            List<AdoptionProject> projects = adoptionProjectMapper.selectByProjectStatus(projectStatus);
            log.debug("查询到{}个状态为{}的项目", projects.size(), projectStatus);
            return projects;
        } catch (Exception e) {
            log.error("根据项目状态查询项目列表失败，状态: {}", projectStatus, e);
            throw new BusinessException("查询项目列表失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createProject(AdoptionProject adoptionProject) {
        if (adoptionProject == null) {
            throw new BusinessException("项目信息不能为空");
        }
        
        // 验证必填字段
        validateProjectFields(adoptionProject);
        
        log.info("创建认养项目: {}", adoptionProject.getName());
        try {
            // 验证关联数据是否存在
            validateRelatedData(adoptionProject);
            
            // 验证项目名称唯一性
            if (existsByName(adoptionProject.getName(), null)) {
                throw new BusinessException("项目名称已存在");
            }
            
            // 设置默认值
            setDefaultValues(adoptionProject);
            
            boolean result = save(adoptionProject);
            if (result) {
                // 创建项目单元
                boolean unitsCreated = projectUnitService.batchCreateUnits(
                    adoptionProject.getId(), adoptionProject.getTotalUnits());
                
                if (!unitsCreated) {
                    log.error("创建项目单元失败");
                    throw new BusinessException("创建项目单元失败");
                }
                
                log.info("认养项目创建成功，ID: {}", adoptionProject.getId());
            } else {
                log.error("认养项目创建失败");
                throw new BusinessException("认养项目创建失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建认养项目失败", e);
            throw new BusinessException("创建认养项目失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProject(AdoptionProject adoptionProject) {
        if (adoptionProject == null || adoptionProject.getId() == null) {
            throw new BusinessException("项目信息不完整");
        }
        
        log.info("更新认养项目: ID={}, Name={}", adoptionProject.getId(), adoptionProject.getName());
        try {
            // 验证项目是否存在
            AdoptionProject existingProject = getById(adoptionProject.getId());
            if (existingProject == null) {
                throw new BusinessException("项目不存在");
            }
            
            // 验证项目名称唯一性
            if (StringUtils.hasText(adoptionProject.getName()) && 
                existsByName(adoptionProject.getName(), adoptionProject.getId())) {
                throw new BusinessException("项目名称已存在");
            }
            
            // 验证业务规则
            validateUpdateRules(existingProject, adoptionProject);
            
            boolean result = updateById(adoptionProject);
            if (result) {
                log.info("认养项目更新成功");
            } else {
                log.error("认养项目更新失败");
                throw new BusinessException("认养项目更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新认养项目失败", e);
            throw new BusinessException("更新认养项目失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProject(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.info("删除认养项目: ID={}", projectId);
        try {
            // 验证项目是否存在
            AdoptionProject project = getById(projectId);
            if (project == null) {
                throw new BusinessException("项目不存在");
            }
            
            // 检查项目状态，只有筹备中的项目才能删除
            if (project.getProjectStatus() != 1) {
                throw new BusinessException("只有筹备中的项目才能删除");
            }
            
            // TODO: 检查是否有认养订单等关联数据
            
            // 删除项目单元
            LambdaQueryWrapper<Object> unitWrapper = new LambdaQueryWrapper<>();
            // projectUnitService.remove(unitWrapper.eq("project_id", projectId));
            
            boolean result = removeById(projectId);
            if (result) {
                log.info("认养项目删除成功");
            } else {
                log.error("认养项目删除失败");
                throw new BusinessException("认养项目删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除认养项目失败", e);
            throw new BusinessException("删除认养项目失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProjectStatus(Long projectId, Integer projectStatus) {
        if (projectId == null || projectStatus == null) {
            throw new BusinessException("参数不能为空");
        }
        
        if (projectStatus < 1 || projectStatus > 6) {
            throw new BusinessException("项目状态值无效");
        }
        
        log.info("更新项目状态: ID={}, Status={}", projectId, projectStatus);
        try {
            // 验证状态流转规则
            AdoptionProject existingProject = getById(projectId);
            if (existingProject == null) {
                throw new BusinessException("项目不存在");
            }
            
            validateStatusTransition(existingProject.getProjectStatus(), projectStatus);
            
            AdoptionProject project = new AdoptionProject();
            project.setId(projectId);
            project.setProjectStatus(projectStatus);
            
            boolean result = updateById(project);
            if (result) {
                log.info("项目状态更新成功");
                
                // 根据状态变更执行相应的业务逻辑
                handleStatusChange(projectId, projectStatus);
            } else {
                log.error("项目状态更新失败");
                throw new BusinessException("项目状态更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新项目状态失败", e);
            throw new BusinessException("更新项目状态失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAvailableUnits(Long projectId, Integer unitCount) {
        if (projectId == null || unitCount == null) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("更新项目可用单元数: ID={}, UnitCount={}", projectId, unitCount);
        try {
            int result = adoptionProjectMapper.updateAvailableUnits(projectId, unitCount);
            if (result > 0) {
                log.info("项目可用单元数更新成功");
                return true;
            } else {
                log.error("项目可用单元数更新失败");
                throw new BusinessException("项目可用单元数更新失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新项目可用单元数失败", e);
            throw new BusinessException("更新项目可用单元数失败");
        }
    }

    @Override
    public AdoptionProject getProjectDetail(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("获取项目详情: ID={}", projectId);
        try {
            AdoptionProject project = adoptionProjectMapper.selectProjectDetail(projectId);
            if (project == null) {
                throw new BusinessException("项目不存在");
            }
            return project;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取项目详情失败", e);
            throw new BusinessException("获取项目详情失败");
        }
    }

    @Override
    public boolean existsByName(String name, Long excludeId) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        
        LambdaQueryWrapper<AdoptionProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionProject::getName, name);
        if (excludeId != null) {
            wrapper.ne(AdoptionProject::getId, excludeId);
        }
        
        return count(wrapper) > 0;
    }

    @Override
    public boolean canAdopt(Long projectId, Integer unitCount) {
        if (projectId == null || unitCount == null || unitCount <= 0) {
            return false;
        }
        
        log.debug("检查项目是否可以认养: projectId={}, unitCount={}", projectId, unitCount);
        try {
            AdoptionProject project = getById(projectId);
            if (project == null) {
                return false;
            }
            
            // 检查项目状态是否为认养中
            if (project.getProjectStatus() != 2) {
                log.debug("项目状态不是认养中，无法认养");
                return false;
            }
            
            // 检查可用单元数是否足够
            if (project.getAvailableUnits() < unitCount) {
                log.debug("可用单元数不足，需要: {}, 可用: {}", unitCount, project.getAvailableUnits());
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("检查项目是否可以认养失败", e);
            return false;
        }
    }

    /**
     * 验证项目必填字段
     */
    private void validateProjectFields(AdoptionProject project) {
        if (project.getPlotId() == null) {
            throw new BusinessException("地块ID不能为空");
        }
        
        if (project.getCropId() == null) {
            throw new BusinessException("作物ID不能为空");
        }
        
        if (!StringUtils.hasText(project.getName())) {
            throw new BusinessException("项目名称不能为空");
        }
        
        if (project.getTotalUnits() == null || project.getTotalUnits() <= 0) {
            throw new BusinessException("总单元数必须大于0");
        }
        
        if (project.getUnitArea() == null || project.getUnitArea().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("单元面积必须大于0");
        }
        
        if (project.getUnitPrice() == null || project.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("单元价格必须大于0");
        }
    }

    /**
     * 验证关联数据是否存在
     */
    private void validateRelatedData(AdoptionProject project) {
        // 验证地块是否存在
        if (farmPlotService.getById(project.getPlotId()) == null) {
            throw new BusinessException("地块不存在");
        }
        
        // 验证作物是否存在
        if (cropService.getById(project.getCropId()) == null) {
            throw new BusinessException("作物不存在");
        }
    }

    /**
     * 设置默认值
     */
    private void setDefaultValues(AdoptionProject project) {
        if (project.getProjectStatus() == null) {
            project.setProjectStatus(1); // 默认筹备中状态
        }
        
        if (project.getAvailableUnits() == null) {
            project.setAvailableUnits(project.getTotalUnits());
        }
        
        if (project.getPlantingDate() == null) {
            project.setPlantingDate(LocalDate.now().plusDays(30)); // 默认30天后种植
        }
    }

    /**
     * 验证更新规则
     */
    private void validateUpdateRules(AdoptionProject existing, AdoptionProject updated) {
        // 如果项目已经开始认养，某些字段不能修改
        if (existing.getProjectStatus() >= 2) {
            if (updated.getTotalUnits() != null && !updated.getTotalUnits().equals(existing.getTotalUnits())) {
                throw new BusinessException("项目已开始认养，不能修改总单元数");
            }
            
            if (updated.getUnitPrice() != null && updated.getUnitPrice().compareTo(existing.getUnitPrice()) != 0) {
                throw new BusinessException("项目已开始认养，不能修改单元价格");
            }
        }
    }

    /**
     * 验证状态流转规则
     */
    private void validateStatusTransition(Integer currentStatus, Integer newStatus) {
        // 定义允许的状态流转规则
        // 1-筹备中 -> 2-认养中
        // 2-认养中 -> 3-种植中
        // 3-种植中 -> 4-收获中
        // 4-收获中 -> 5-已完成
        // 任何状态 -> 6-已取消
        
        if (currentStatus.equals(newStatus)) {
            return; // 状态未变更
        }
        
        boolean validTransition = switch (currentStatus) {
            case 1 -> // 筹备中
                    newStatus == 2 || newStatus == 6;
            case 2 -> // 认养中
                    newStatus == 3 || newStatus == 6;
            case 3 -> // 种植中
                    newStatus == 4 || newStatus == 6;
            case 4 -> // 收获中
                    newStatus == 5 || newStatus == 6;
            case 5 -> // 已完成
                    false; // 已完成状态不能变更
            case 6 -> // 已取消
                    false;
            default -> false; // 已取消状态不能变更
        };

        if (!validTransition) {
            throw new BusinessException("无效的状态流转");
        }
    }

    /**
     * 处理状态变更后的业务逻辑
     */
    private void handleStatusChange(Long projectId, Integer newStatus) {
        switch (newStatus) {
            case 3: // 种植中
                startPlanting(projectId);
                break;
            case 4: // 收获中
                startHarvesting(projectId);
                break;
            case 5: // 已完成
                completeProject(projectId);
                break;
        }
    }

    @Override
    public boolean startPlanting(Long projectId) {
        log.info("开始项目种植: projectId={}", projectId);
        try {
            // 更新认养记录状态为种植中
            // TODO: 调用AdoptionRecordService更新状态
            
            // 更新项目单元状态为种植中
            List<Long> unitIds = projectUnitService.getUnitsByProjectIdAndStatus(projectId, 2)
                    .stream().map(ProjectUnit::getId).toList();
            
            if (!unitIds.isEmpty()) {
                projectUnitService.startPlanting(unitIds);
            }
            
            return true;
        } catch (Exception e) {
            log.error("开始项目种植失败", e);
            return false;
        }
    }

    @Override
    public boolean startHarvesting(Long projectId) {
        log.info("开始项目收获: projectId={}", projectId);
        try {
            // 更新认养记录状态为待收获
            // TODO: 调用AdoptionRecordService更新状态
            
            return true;
        } catch (Exception e) {
            log.error("开始项目收获失败", e);
            return false;
        }
    }

    @Override
    public boolean completeProject(Long projectId) {
        log.info("完成项目: projectId={}", projectId);
        try {
            // 更新认养记录状态为已完成
            // TODO: 调用AdoptionRecordService更新状态
            
            // 更新项目单元状态为已收获
            List<Long> unitIds = projectUnitService.getUnitsByProjectIdAndStatus(projectId, 4)
                    .stream().map(ProjectUnit::getId).toList();
            
            if (!unitIds.isEmpty()) {
                projectUnitService.completeHarvest(unitIds);
            }
            
            return true;
        } catch (Exception e) {
            log.error("完成项目失败", e);
            return false;
        }
    }

    @Override
    public List<AdoptionProject> getPopularProjects(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        log.debug("获取热门项目列表，限制数量: {}", limit);
        try {
            // TODO: 根据认养数量、评分等因素排序
            LambdaQueryWrapper<AdoptionProject> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdoptionProject::getProjectStatus, 2); // 认养中的项目
            wrapper.orderByDesc(AdoptionProject::getCreateTime);
            wrapper.last("LIMIT " + limit);

            List<AdoptionProject> projects = list(wrapper);
            log.debug("获取到{}个热门项目", projects.size());
            return projects;
        } catch (Exception e) {
            log.error("获取热门项目列表失败", e);
            throw new BusinessException("获取热门项目列表失败");
        }
    }

    @Override
    public List<AdoptionProject> getRecommendedProjects(Long userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        log.debug("获取推荐项目列表，用户ID: {}, 限制数量: {}", userId, limit);
        try {
            // TODO: 根据用户偏好、历史认养记录等推荐
            LambdaQueryWrapper<AdoptionProject> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdoptionProject::getProjectStatus, 2); // 认养中的项目
            wrapper.gt(AdoptionProject::getAvailableUnits, 0); // 有可用单元
            wrapper.orderByDesc(AdoptionProject::getCreateTime);
            wrapper.last("LIMIT " + limit);

            List<AdoptionProject> projects = list(wrapper);
            log.debug("获取到{}个推荐项目", projects.size());
            return projects;
        } catch (Exception e) {
            log.error("获取推荐项目列表失败", e);
            throw new BusinessException("获取推荐项目列表失败");
        }
    }

    @Override
    public IPage<AdoptionProject> searchProjects(String keyword, Page<AdoptionProject> page) {
        if (!StringUtils.hasText(keyword)) {
            throw new BusinessException("搜索关键词不能为空");
        }

        log.debug("搜索项目: keyword={}", keyword);
        try {
            LambdaQueryWrapper<AdoptionProject> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdoptionProject::getProjectStatus, 2); // 只搜索认养中的项目
            wrapper.and(w -> w.like(AdoptionProject::getName, keyword)
                    .or().like(AdoptionProject::getDescription, keyword));
            wrapper.orderByDesc(AdoptionProject::getCreateTime);

            IPage<AdoptionProject> result = page(page, wrapper);
            log.debug("搜索到{}条项目记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("搜索项目失败", e);
            throw new BusinessException("搜索项目失败");
        }
    }

    @Override
    public Object getProjectStatistics(Long farmId) {
        log.debug("获取项目统计信息，农场ID: {}", farmId);
        try {
            Map<String, Object> statistics = new HashMap<>();

            LambdaQueryWrapper<AdoptionProject> baseWrapper = new LambdaQueryWrapper<>();
            if (farmId != null) {
                // 通过地块关联查询特定农场的项目
                // TODO: 需要join查询farm_plots表
                // 这里简化处理
            }

            // 状态统计
            statistics.put("totalCount", count(baseWrapper));
            statistics.put("preparingCount", count(baseWrapper.clone().eq(AdoptionProject::getProjectStatus, 1)));
            statistics.put("adoptingCount", count(baseWrapper.clone().eq(AdoptionProject::getProjectStatus, 2)));
            statistics.put("plantingCount", count(baseWrapper.clone().eq(AdoptionProject::getProjectStatus, 3)));
            statistics.put("harvestingCount", count(baseWrapper.clone().eq(AdoptionProject::getProjectStatus, 4)));
            statistics.put("completedCount", count(baseWrapper.clone().eq(AdoptionProject::getProjectStatus, 5)));
            statistics.put("cancelledCount", count(baseWrapper.clone().eq(AdoptionProject::getProjectStatus, 6)));

            return statistics;
        } catch (Exception e) {
            log.error("获取项目统计信息失败", e);
            throw new BusinessException("获取项目统计信息失败");
        }
    }

    @Override
    public boolean hasPermission(Long userId, Long projectId) {
        if (userId == null || projectId == null) {
            return false;
        }

        log.debug("检查用户项目操作权限: userId={}, projectId={}", userId, projectId);
        try {
            // 获取项目信息
            AdoptionProject project = getById(projectId);
            if (project == null) {
                return false;
            }

            // 检查用户是否为该项目所属农场的农场主
            return farmPlotService.hasPermission(userId, project.getPlotId());
        } catch (Exception e) {
            log.error("检查用户项目操作权限失败", e);
            return false;
        }
    }

    // ========== 新增的DTO/VO相关方法实现 ==========

    @Override
    public IPage<AdoptionProjectVO> getAdoptionProjects(Integer current, Integer size, AdoptionProjectQueryDTO queryDTO) {
        // TODO: 实现分页查询认养项目列表（VO版本）
        throw new BusinessException("方法暂未实现");
    }

    @Override
    public AdoptionProjectVO getAdoptionProjectById(Long id) {
        // TODO: 实现根据ID获取认养项目详情（VO版本）
        throw new BusinessException("方法暂未实现");
    }

    @Override
    public AdoptionProjectVO createAdoptionProject(AdoptionProjectCreateDTO createDTO, Long userId) {
        // TODO: 实现创建认养项目（DTO版本）
        throw new BusinessException("方法暂未实现");
    }

    @Override
    public AdoptionProjectVO updateAdoptionProject(Long id, AdoptionProjectUpdateDTO updateDTO, Long userId) {
        // TODO: 实现更新认养项目（DTO版本）
        throw new BusinessException("方法暂未实现");
    }

    @Override
    public void deleteAdoptionProject(Long id, Long userId) {
        // TODO: 实现删除认养项目（VO版本）
        throw new BusinessException("方法暂未实现");
    }

    @Override
    public List<ProjectUnitVO> getProjectUnits(Long projectId, Integer unitStatus) {
        // TODO: 实现获取项目单元列表
        throw new BusinessException("方法暂未实现");
    }

    @Override
    public List<AdoptionProjectVO> getPopularProjectsVO(Integer limit) {
        // TODO: 实现获取热门认养项目（VO版本）
        throw new BusinessException("方法暂未实现");
    }

    @Override
    public IPage<AdoptionProjectVO> searchAdoptionProjects(Integer current, Integer size, AdoptionProjectQueryDTO queryDTO) {
        // TODO: 实现搜索认养项目（VO版本）
        throw new BusinessException("方法暂未实现");
    }
}
