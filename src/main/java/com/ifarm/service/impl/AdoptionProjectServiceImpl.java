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
import com.ifarm.entity.Crop;
import com.ifarm.entity.Farm;
import com.ifarm.entity.FarmPlot;
import com.ifarm.entity.ProjectUnit;
import com.ifarm.vo.adoption.AdoptionProjectVO;
import com.ifarm.vo.adoption.ProjectUnitVO;
import com.ifarm.mapper.AdoptionProjectMapper;
import com.ifarm.service.IAdoptionProjectService;
import com.ifarm.service.ICropService;
import com.ifarm.service.IFarmPlotService;
import com.ifarm.service.IFarmService;
import com.ifarm.service.IProjectUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final IFarmService farmService;

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
        if (current == null || current < 1) {
            current = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }

        log.info("分页查询认养项目: current={}, size={}, queryDTO={}", current, size, queryDTO);
        try {
            // 构建查询条件
            LambdaQueryWrapper<AdoptionProject> wrapper = new LambdaQueryWrapper<>();

            if (queryDTO != null) {
                // 项目名称模糊查询
                if (StringUtils.hasText(queryDTO.getName())) {
                    wrapper.like(AdoptionProject::getName, queryDTO.getName());
                }

                // 农场ID
                if (queryDTO.getFarmId() != null) {
                    // 需要通过地块查询农场ID，这里先跳过
                    // wrapper.eq(AdoptionProject::getFarmId, queryDTO.getFarmId());
                }

                // 地块ID
                if (queryDTO.getPlotId() != null) {
                    wrapper.eq(AdoptionProject::getPlotId, queryDTO.getPlotId());
                }

                // 作物ID
                if (queryDTO.getCropId() != null) {
                    wrapper.eq(AdoptionProject::getCropId, queryDTO.getCropId());
                }

                // 项目状态
                if (queryDTO.getProjectStatus() != null) {
                    wrapper.eq(AdoptionProject::getProjectStatus, queryDTO.getProjectStatus());
                }

                // 价格范围
                if (queryDTO.getMinPrice() != null) {
                    wrapper.ge(AdoptionProject::getUnitPrice, queryDTO.getMinPrice());
                }
                if (queryDTO.getMaxPrice() != null) {
                    wrapper.le(AdoptionProject::getUnitPrice, queryDTO.getMaxPrice());
                }

                // 开始时间范围
                if (queryDTO.getStartTimeBegin() != null) {
                    wrapper.ge(AdoptionProject::getPlantingDate, queryDTO.getStartTimeBegin().toLocalDate());
                }
                if (queryDTO.getStartTimeEnd() != null) {
                    wrapper.le(AdoptionProject::getPlantingDate, queryDTO.getStartTimeEnd().toLocalDate());
                }

                // 是否有可用单元
                if (queryDTO.getHasAvailableUnits() != null && queryDTO.getHasAvailableUnits()) {
                    wrapper.gt(AdoptionProject::getAvailableUnits, 0);
                }

                // 关键词搜索
                if (StringUtils.hasText(queryDTO.getKeyword())) {
                    wrapper.and(w -> w.like(AdoptionProject::getName, queryDTO.getKeyword())
                                    .or().like(AdoptionProject::getDescription, queryDTO.getKeyword()));
                }
            }

            // 排序
            if (queryDTO != null && StringUtils.hasText(queryDTO.getSortField())) {
                boolean isAsc = !"desc".equalsIgnoreCase(queryDTO.getSortOrder());
                switch (queryDTO.getSortField()) {
                    case "createTime":
                        wrapper.orderBy(true, isAsc, AdoptionProject::getCreateTime);
                        break;
                    case "adoptionPrice":
                        wrapper.orderBy(true, isAsc, AdoptionProject::getUnitPrice);
                        break;
                    case "totalUnits":
                        wrapper.orderBy(true, isAsc, AdoptionProject::getTotalUnits);
                        break;
                    case "startTime":
                        wrapper.orderBy(true, isAsc, AdoptionProject::getPlantingDate);
                        break;
                    default:
                        wrapper.orderByDesc(AdoptionProject::getCreateTime);
                        break;
                }
            } else {
                // 默认按创建时间倒序
                wrapper.orderByDesc(AdoptionProject::getCreateTime);
            }

            // 分页查询
            Page<AdoptionProject> page = new Page<>(current, size);
            IPage<AdoptionProject> projectPage = page(page, wrapper);

            // 转换为VO
            IPage<AdoptionProjectVO> voPage = projectPage.convert(this::convertToVO);

            log.info("分页查询认养项目成功: total={}, pages={}", voPage.getTotal(), voPage.getPages());
            return voPage;

        } catch (Exception e) {
            log.error("分页查询认养项目失败: current={}, size={}, queryDTO={}", current, size, queryDTO, e);
            throw new BusinessException("查询认养项目失败");
        }
    }

    @Override
    public AdoptionProjectVO getAdoptionProjectById(Long id) {
        if (id == null) {
            throw new BusinessException("项目ID不能为空");
        }

        log.info("获取认养项目详情: id={}", id);
        try {
            // 查询项目基本信息
            AdoptionProject project = getById(id);
            if (project == null) {
                throw new BusinessException("认养项目不存在");
            }

            // 转换为VO对象
            AdoptionProjectVO projectVO = convertToVO(project);

            log.info("获取认养项目详情成功: id={}, name={}", id, project.getName());
            return projectVO;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取认养项目详情失败: id={}", id, e);
            throw new BusinessException("获取认养项目详情失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdoptionProjectVO createAdoptionProject(AdoptionProjectCreateDTO createDTO, Long userId) {
        if (createDTO == null) {
            throw new BusinessException("创建信息不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        log.info("创建认养项目: userId={}, createDTO={}", userId, createDTO);
        try {
            // 验证地块是否存在且可用
            FarmPlot plot = farmPlotService.getById(createDTO.getPlotId());
            if (plot == null) {
                throw new BusinessException("地块不存在");
            }
            if (plot.getStatus() == null || plot.getStatus() != 1) {
                throw new BusinessException("地块不可用");
            }

            // 验证作物是否存在
            Crop crop = cropService.getById(createDTO.getCropId());
            if (crop == null) {
                throw new BusinessException("作物不存在");
            }

            // 验证时间范围
            if (createDTO.getEndTime().isBefore(createDTO.getStartTime())) {
                throw new BusinessException("结束时间不能早于开始时间");
            }

            // 创建认养项目实体
            AdoptionProject project = new AdoptionProject();
            BeanUtils.copyProperties(createDTO, project);

            // 处理字段映射
            project.setPlotId(createDTO.getPlotId());
            project.setCropId(createDTO.getCropId());
            project.setUnitPrice(createDTO.getAdoptionPrice());
            project.setPlantingDate(createDTO.getStartTime().toLocalDate());
            project.setExpectedHarvestDate(createDTO.getEndTime().toLocalDate());
            project.setProjectStatus(1); // 默认为草稿状态
            project.setAvailableUnits(createDTO.getTotalUnits()); // 初始可用单元数等于总单元数
            project.setCareInstructions(createDTO.getRemark());
            project.setImages(createDTO.getImages());

            // 保存项目
            boolean saved = save(project);
            if (!saved) {
                throw new BusinessException("创建认养项目失败");
            }

            // 自动生成项目单元
            generateProjectUnits(project.getId(), createDTO.getTotalUnits(), createDTO.getUnitArea());

            log.info("创建认养项目成功: projectId={}, name={}", project.getId(), project.getName());

            // 返回VO对象
            return getAdoptionProjectById(project.getId());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建认养项目失败: userId={}, createDTO={}", userId, createDTO, e);
            throw new BusinessException("创建认养项目失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdoptionProjectVO updateAdoptionProject(Long id, AdoptionProjectUpdateDTO updateDTO, Long userId) {
        if (id == null) {
            throw new BusinessException("项目ID不能为空");
        }
        if (updateDTO == null) {
            throw new BusinessException("更新信息不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        log.info("更新认养项目: id={}, userId={}, updateDTO={}", id, userId, updateDTO);
        try {
            // 查询现有项目
            AdoptionProject existingProject = getById(id);
            if (existingProject == null) {
                throw new BusinessException("认养项目不存在");
            }

            // 检查权限（只有管理员或农场主可以更新）
            // TODO: 实现更细粒度的权限检查

            // 检查项目状态是否允许更新
            if (existingProject.getProjectStatus() != null && existingProject.getProjectStatus() > 3) {
                throw new BusinessException("项目已完成或已取消，不能修改");
            }

            // 复制更新字段
            if (StringUtils.hasText(updateDTO.getName())) {
                existingProject.setName(updateDTO.getName());
            }
            if (StringUtils.hasText(updateDTO.getDescription())) {
                existingProject.setDescription(updateDTO.getDescription());
            }
            if (updateDTO.getAdoptionPrice() != null) {
                existingProject.setUnitPrice(updateDTO.getAdoptionPrice());
            }
            if (updateDTO.getTotalUnits() != null) {
                // 更新总单元数需要特殊处理
                updateTotalUnits(existingProject, updateDTO.getTotalUnits());
            }
            if (updateDTO.getUnitArea() != null) {
                existingProject.setUnitArea(updateDTO.getUnitArea());
            }
            if (updateDTO.getExpectedYield() != null) {
                existingProject.setExpectedYield(updateDTO.getExpectedYield());
            }
            if (updateDTO.getAdoptionDays() != null) {
                // 根据认养周期更新结束时间
                if (existingProject.getPlantingDate() != null) {
                    existingProject.setExpectedHarvestDate(
                        existingProject.getPlantingDate().plusDays(updateDTO.getAdoptionDays()));
                }
            }
            if (updateDTO.getStartTime() != null) {
                existingProject.setPlantingDate(updateDTO.getStartTime().toLocalDate());
            }
            if (updateDTO.getEndTime() != null) {
                existingProject.setExpectedHarvestDate(updateDTO.getEndTime().toLocalDate());
            }
            if (updateDTO.getProjectStatus() != null) {
                existingProject.setProjectStatus(updateDTO.getProjectStatus());
            }
            if (StringUtils.hasText(updateDTO.getImages())) {
                existingProject.setImages(updateDTO.getImages());
            }
            if (StringUtils.hasText(updateDTO.getTags())) {
                // 标签可以存储在其他字段或扩展表中，这里暂时跳过
            }
            if (StringUtils.hasText(updateDTO.getRemark())) {
                existingProject.setCareInstructions(updateDTO.getRemark());
            }

            // 保存更新
            boolean updated = updateById(existingProject);
            if (!updated) {
                throw new BusinessException("更新认养项目失败");
            }

            log.info("更新认养项目成功: id={}, name={}", id, existingProject.getName());

            // 返回更新后的VO对象
            return getAdoptionProjectById(id);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新认养项目失败: id={}, userId={}, updateDTO={}", id, userId, updateDTO, e);
            throw new BusinessException("更新认养项目失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdoptionProject(Long id, Long userId) {
        if (id == null) {
            throw new BusinessException("项目ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        log.info("删除认养项目: id={}, userId={}", id, userId);
        try {
            // 查询项目是否存在
            AdoptionProject project = getById(id);
            if (project == null) {
                throw new BusinessException("认养项目不存在");
            }

            // 检查权限（只有管理员可以删除）
            // TODO: 实现更细粒度的权限检查

            // 检查项目状态是否允许删除
            if (project.getProjectStatus() != null && project.getProjectStatus() != 1) {
                throw new BusinessException("只有草稿状态的项目才能删除");
            }

            // 检查是否有已认养的单元
            LambdaQueryWrapper<ProjectUnit> unitWrapper = new LambdaQueryWrapper<>();
            unitWrapper.eq(ProjectUnit::getProjectId, id)
                       .eq(ProjectUnit::getUnitStatus, 2); // 2-已认养
            long adoptedCount = projectUnitService.count(unitWrapper);
            if (adoptedCount > 0) {
                throw new BusinessException("项目存在已认养单元，不能删除");
            }

            // TODO: 检查是否有认养订单等关联数据

            // 删除项目单元
            LambdaQueryWrapper<ProjectUnit> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(ProjectUnit::getProjectId, id);
            boolean unitsDeleted = projectUnitService.remove(deleteWrapper);
            if (!unitsDeleted) {
                log.warn("删除项目单元失败，但继续删除项目: projectId={}", id);
            }

            // 删除项目
            boolean projectDeleted = removeById(id);
            if (!projectDeleted) {
                throw new BusinessException("删除认养项目失败");
            }

            log.info("删除认养项目成功: id={}, name={}", id, project.getName());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除认养项目失败: id={}, userId={}", id, userId, e);
            throw new BusinessException("删除认养项目失败");
        }
    }

    @Override
    public List<ProjectUnitVO> getProjectUnits(Long projectId, Integer unitStatus) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }

        log.info("获取项目单元列表: projectId={}, unitStatus={}", projectId, unitStatus);
        try {
            // 构建查询条件
            LambdaQueryWrapper<ProjectUnit> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProjectUnit::getProjectId, projectId);

            if (unitStatus != null) {
                wrapper.eq(ProjectUnit::getUnitStatus, unitStatus);
            }

            wrapper.orderBy(true, true, ProjectUnit::getUnitNumber);

            List<ProjectUnit> units = projectUnitService.list(wrapper);

            // 转换为VO
            List<ProjectUnitVO> voList = units.stream()
                    .map(this::convertUnitToVO)
                    .collect(java.util.stream.Collectors.toList());

            log.info("获取项目单元列表成功: projectId={}, count={}", projectId, voList.size());
            return voList;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取项目单元列表失败: projectId={}, unitStatus={}", projectId, unitStatus, e);
            throw new BusinessException("获取项目单元列表失败");
        }
    }

    @Override
    public List<AdoptionProjectVO> getPopularProjectsVO(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        log.info("获取热门认养项目: limit={}", limit);
        try {
            // 构建查询条件：只查询认养中的项目，按认养率排序
            LambdaQueryWrapper<AdoptionProject> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdoptionProject::getProjectStatus, 2) // 认养中
                   .gt(AdoptionProject::getTotalUnits, 0) // 有单元的项目
                   .orderByDesc(AdoptionProject::getTotalUnits, AdoptionProject::getAvailableUnits) // 按认养率排序（总数-可用数）
                   .orderByDesc(AdoptionProject::getCreateTime) // 再按创建时间排序
                   .last("LIMIT " + limit);

            List<AdoptionProject> projects = list(wrapper);

            // 转换为VO
            List<AdoptionProjectVO> voList = projects.stream()
                    .map(this::convertToVO)
                    .collect(java.util.stream.Collectors.toList());

            log.info("获取热门认养项目成功: count={}", voList.size());
            return voList;

        } catch (Exception e) {
            log.error("获取热门认养项目失败: limit={}", limit, e);
            throw new BusinessException("获取热门认养项目失败");
        }
    }

    @Override
    public IPage<AdoptionProjectVO> searchAdoptionProjects(Integer current, Integer size, AdoptionProjectQueryDTO queryDTO) {
        // 搜索项目实际上就是带条件的分页查询，直接复用getAdoptionProjects方法
        return getAdoptionProjects(current, size, queryDTO);
    }

    // ========== 私有辅助方法 ==========

    /**
     * 将AdoptionProject实体转换为AdoptionProjectVO
     */
    private AdoptionProjectVO convertToVO(AdoptionProject project) {
        if (project == null) {
            return null;
        }

        AdoptionProjectVO vo = new AdoptionProjectVO();

        // 基本信息
        vo.setId(project.getId());
        vo.setName(project.getName());
        vo.setDescription(project.getDescription());
        vo.setTotalUnits(project.getTotalUnits());
        vo.setAvailableUnits(project.getAvailableUnits());
        vo.setAdoptedUnits(project.getTotalUnits() - project.getAvailableUnits());
        vo.setUnitArea(project.getUnitArea());
        vo.setAdoptionPrice(project.getUnitPrice());
        vo.setExpectedYield(project.getExpectedYield());
        vo.setStartTime(project.getPlantingDate() != null ? project.getPlantingDate().atStartOfDay() : null);
        vo.setEndTime(project.getExpectedHarvestDate() != null ? project.getExpectedHarvestDate().atStartOfDay() : null);
        vo.setProjectStatus(project.getProjectStatus());
        vo.setProjectStatusText(getProjectStatusText(project.getProjectStatus()));
        vo.setRemark(project.getCareInstructions());
        vo.setCreateTime(project.getCreateTime());
        vo.setUpdateTime(project.getUpdateTime());

        // 计算认养进度
        if (project.getTotalUnits() != null && project.getTotalUnits() > 0) {
            int adoptedUnits = project.getTotalUnits() - (project.getAvailableUnits() != null ? project.getAvailableUnits() : 0);
            vo.setAdoptionProgress(BigDecimal.valueOf(adoptedUnits * 100.0 / project.getTotalUnits()).setScale(1, RoundingMode.HALF_UP));
        } else {
            vo.setAdoptionProgress(BigDecimal.ZERO);
        }

        // 判断是否可以认养
        vo.setCanAdopt(project.getProjectStatus() != null && project.getProjectStatus() == 2 &&
                      project.getAvailableUnits() != null && project.getAvailableUnits() > 0);

        // 计算剩余认养时间（如果有结束时间）
        if (project.getExpectedHarvestDate() != null) {
            long remainingDays = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), project.getExpectedHarvestDate());
            vo.setRemainingDays(Math.max(0, remainingDays));
        }

        // 处理图片列表
        if (StringUtils.hasText(project.getImages())) {
            try {
                // 简单的JSON解析，假设是字符串数组格式
                String images = project.getImages().trim();
                if (images.startsWith("[") && images.endsWith("]")) {
                    images = images.substring(1, images.length() - 1);
                    String[] imageArray = images.split(",");
                    List<String> imageList = new ArrayList<>();
                    for (String img : imageArray) {
                        String cleanImg = img.trim().replaceAll("\"", "");
                        if (StringUtils.hasText(cleanImg)) {
                            imageList.add(cleanImg);
                        }
                    }
                    vo.setImages(imageList);
                }
            } catch (Exception e) {
                log.warn("解析项目图片失败: projectId={}, images={}", project.getId(), project.getImages(), e);
                vo.setImages(new ArrayList<>());
            }
        } else {
            vo.setImages(new ArrayList<>());
        }

        // 处理标签列表（暂时设置为空，后续可以从其他字段获取）
        vo.setTags(new ArrayList<>());

        // 查询关联信息
        try {
            // 查询地块信息
            if (project.getPlotId() != null) {
                FarmPlot plot = farmPlotService.getById(project.getPlotId());
                if (plot != null) {
                    vo.setPlot(convertPlotToVO(plot));

                    // 通过地块查询农场信息
                    if (plot.getFarmId() != null) {
                        Farm farm = farmService.getById(plot.getFarmId());
                        if (farm != null) {
                            vo.setFarm(convertFarmToVO(farm));
                        }
                    }
                }
            }

            // 查询作物信息
            if (project.getCropId() != null) {
                Crop crop = cropService.getById(project.getCropId());
                if (crop != null) {
                    vo.setCrop(convertCropToVO(crop));
                }
            }
        } catch (Exception e) {
            log.warn("查询项目关联信息失败: projectId={}", project.getId(), e);
        }

        return vo;
    }

    /**
     * 获取项目状态文本
     */
    private String getProjectStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1: return "草稿";
            case 2: return "认养中";
            case 3: return "已满员";
            case 4: return "进行中";
            case 5: return "已完成";
            case 6: return "已取消";
            default: return "未知";
        }
    }

    /**
     * 转换地块为VO
     */
    private com.ifarm.vo.farmplot.FarmPlotVO convertPlotToVO(FarmPlot plot) {
        if (plot == null) {
            return null;
        }

        com.ifarm.vo.farmplot.FarmPlotVO vo = new com.ifarm.vo.farmplot.FarmPlotVO();
        BeanUtils.copyProperties(plot, vo);

        // 处理字段名不匹配的情况
        vo.setPlotName(plot.getName());
        vo.setLocation(plot.getLocationInfo());
        vo.setEnabled(plot.getStatus() != null && plot.getStatus() == 1);
        vo.setPlotStatus(plot.getStatus());
        vo.setPlotStatusName(getPlotStatusText(plot.getStatus()));

        return vo;
    }

    /**
     * 转换农场为VO
     */
    private com.ifarm.vo.farm.FarmVO convertFarmToVO(Farm farm) {
        if (farm == null) {
            return null;
        }

        com.ifarm.vo.farm.FarmVO vo = new com.ifarm.vo.farm.FarmVO();
        BeanUtils.copyProperties(farm, vo);

        // 处理字段名不匹配的情况
        vo.setFarmName(farm.getName());
        vo.setArea(farm.getTotalArea());
        vo.setFarmImage(farm.getCoverImage());
        vo.setEnabled(farm.getStatus() != null && farm.getStatus() == 1);
        vo.setCertificationInfo(farm.getCertification());

        return vo;
    }

    /**
     * 转换作物为VO
     */
    private com.ifarm.vo.crop.CropVO convertCropToVO(Crop crop) {
        if (crop == null) {
            return null;
        }

        com.ifarm.vo.crop.CropVO vo = new com.ifarm.vo.crop.CropVO();
        BeanUtils.copyProperties(crop, vo);

        // 处理字段名不匹配的情况
        vo.setCropName(crop.getName());

        return vo;
    }

    /**
     * 获取地块状态文本
     */
    private String getPlotStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1: return "空闲";
            case 2: return "种植中";
            case 3: return "收获中";
            case 4: return "休耕";
            default: return "未知";
        }
    }

    /**
     * 自动生成项目单元
     */
    private void generateProjectUnits(Long projectId, Integer totalUnits, BigDecimal unitArea) {
        if (projectId == null || totalUnits == null || totalUnits <= 0) {
            return;
        }

        log.info("开始生成项目单元: projectId={}, totalUnits={}", projectId, totalUnits);
        try {
            List<ProjectUnit> units = new ArrayList<>();
            for (int i = 1; i <= totalUnits; i++) {
                ProjectUnit unit = new ProjectUnit();
                unit.setProjectId(projectId);
                unit.setUnitNumber(String.format("P%d-%03d", projectId, i));
                unit.setUnitStatus(1); // 1-可认养

                // 设置位置信息（包含单元面积）
                int row = (i - 1) / 10 + 1; // 每行10个单元
                int col = (i - 1) % 10 + 1;
                String locationInfo = String.format(
                    "{\"row\": %d, \"column\": %d, \"coordinates\": \"%c%d\", \"unitArea\": %s}",
                    row, col, (char)('A' + row - 1), col, unitArea.toString());
                unit.setLocationInfo(locationInfo);

                units.add(unit);
            }

            // 批量保存单元
            boolean saved = projectUnitService.saveBatch(units);
            if (!saved) {
                throw new BusinessException("生成项目单元失败");
            }

            log.info("生成项目单元成功: projectId={}, count={}", projectId, units.size());
        } catch (Exception e) {
            log.error("生成项目单元失败: projectId={}, totalUnits={}", projectId, totalUnits, e);
            throw new BusinessException("生成项目单元失败");
        }
    }

    /**
     * 转换项目单元为VO
     */
    private ProjectUnitVO convertUnitToVO(ProjectUnit unit) {
        if (unit == null) {
            return null;
        }

        ProjectUnitVO vo = new ProjectUnitVO();
        BeanUtils.copyProperties(unit, vo);

        // 处理状态文本
        vo.setUnitStatusText(getUnitStatusText(unit.getUnitStatus()));

        // 从locationInfo中解析单元面积
        if (StringUtils.hasText(unit.getLocationInfo())) {
            try {
                // 简单的JSON解析，提取unitArea
                String locationInfo = unit.getLocationInfo();
                if (locationInfo.contains("\"unitArea\"")) {
                    String[] parts = locationInfo.split("\"unitArea\":");
                    if (parts.length > 1) {
                        String areaStr = parts[1].trim().replaceAll("[}\"\\s]", "");
                        if (areaStr.contains(",")) {
                            areaStr = areaStr.substring(0, areaStr.indexOf(","));
                        }
                        vo.setUnitArea(new BigDecimal(areaStr));
                    }
                }
            } catch (Exception e) {
                log.warn("解析单元面积失败: unitId={}, locationInfo={}", unit.getId(), unit.getLocationInfo(), e);
            }
        }

        // TODO: 查询认养用户信息（如果已认养）
        // 这里需要查询认养记录表来获取认养用户信息

        return vo;
    }

    /**
     * 获取单元状态文本
     */
    private String getUnitStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1: return "可认养";
            case 2: return "已认养";
            case 3: return "种植中";
            case 4: return "待收获";
            case 5: return "已收获";
            default: return "未知";
        }
    }

    /**
     * 更新项目总单元数
     */
    private void updateTotalUnits(AdoptionProject project, Integer newTotalUnits) {
        if (project == null || newTotalUnits == null || newTotalUnits <= 0) {
            return;
        }

        Integer currentTotalUnits = project.getTotalUnits();
        if (currentTotalUnits == null) {
            currentTotalUnits = 0;
        }

        log.info("更新项目总单元数: projectId={}, 当前={}, 新值={}", project.getId(), currentTotalUnits, newTotalUnits);

        try {
            if (newTotalUnits > currentTotalUnits) {
                // 增加单元数：生成新的单元
                int additionalUnits = newTotalUnits - currentTotalUnits;
                List<ProjectUnit> newUnits = new ArrayList<>();

                for (int i = currentTotalUnits + 1; i <= newTotalUnits; i++) {
                    ProjectUnit unit = new ProjectUnit();
                    unit.setProjectId(project.getId());
                    unit.setUnitNumber(String.format("P%d-%03d", project.getId(), i));
                    unit.setUnitStatus(1); // 1-可认养

                    // 设置位置信息
                    int row = (i - 1) / 10 + 1;
                    int col = (i - 1) % 10 + 1;
                    String locationInfo = String.format(
                        "{\"row\": %d, \"column\": %d, \"coordinates\": \"%c%d\", \"unitArea\": %s}",
                        row, col, (char)('A' + row - 1), col, project.getUnitArea().toString());
                    unit.setLocationInfo(locationInfo);

                    newUnits.add(unit);
                }

                // 批量保存新单元
                boolean saved = projectUnitService.saveBatch(newUnits);
                if (!saved) {
                    throw new BusinessException("生成新增单元失败");
                }

                // 更新可用单元数
                project.setAvailableUnits(project.getAvailableUnits() + additionalUnits);

                log.info("增加项目单元成功: projectId={}, 新增{}个单元", project.getId(), additionalUnits);

            } else if (newTotalUnits < currentTotalUnits) {
                // 减少单元数：删除多余的单元（只能删除未认养的单元）
                int unitsToRemove = currentTotalUnits - newTotalUnits;

                // 查询可删除的单元（状态为1-可认养的单元）
                LambdaQueryWrapper<ProjectUnit> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ProjectUnit::getProjectId, project.getId())
                       .eq(ProjectUnit::getUnitStatus, 1) // 只删除可认养的单元
                       .orderByDesc(ProjectUnit::getUnitNumber) // 从后往前删除
                       .last("LIMIT " + unitsToRemove);

                List<ProjectUnit> unitsToDelete = projectUnitService.list(wrapper);
                if (unitsToDelete.size() < unitsToRemove) {
                    throw new BusinessException("无法减少单元数，部分单元已被认养");
                }

                // 删除单元
                List<Long> unitIds = unitsToDelete.stream()
                        .map(ProjectUnit::getId)
                        .collect(java.util.stream.Collectors.toList());
                boolean deleted = projectUnitService.removeByIds(unitIds);
                if (!deleted) {
                    throw new BusinessException("删除多余单元失败");
                }

                // 更新可用单元数
                project.setAvailableUnits(project.getAvailableUnits() - unitsToRemove);

                log.info("减少项目单元成功: projectId={}, 删除{}个单元", project.getId(), unitsToRemove);
            }

            // 更新总单元数
            project.setTotalUnits(newTotalUnits);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新项目总单元数失败: projectId={}, newTotalUnits={}", project.getId(), newTotalUnits, e);
            throw new BusinessException("更新项目总单元数失败");
        }
    }
}
