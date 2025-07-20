package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.Farm;
import com.ifarm.mapper.FarmMapper;
import com.ifarm.service.IFarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 农场服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FarmServiceImpl extends ServiceImpl<FarmMapper, Farm> implements IFarmService {

    private final FarmMapper farmMapper;

    @Override
    public List<Farm> getFarmsByOwnerId(Long ownerId) {
        if (ownerId == null) {
            throw new BusinessException("农场主ID不能为空");
        }
        
        log.debug("根据农场主ID查询农场列表: {}", ownerId);
        try {
            List<Farm> farms = farmMapper.selectByOwnerId(ownerId);
            log.debug("查询到{}个农场", farms.size());
            return farms;
        } catch (Exception e) {
            log.error("根据农场主ID查询农场列表失败，农场主ID: {}", ownerId, e);
            throw new BusinessException("查询农场列表失败");
        }
    }

    @Override
    public List<Farm> getFarmsByLocation(String province, String city, String district) {
        log.debug("根据地区查询农场列表: province={}, city={}, district={}", province, city, district);
        try {
            List<Farm> farms = farmMapper.selectByLocation(province, city, district);
            log.debug("查询到{}个农场", farms.size());
            return farms;
        } catch (Exception e) {
            log.error("根据地区查询农场列表失败", e);
            throw new BusinessException("查询农场列表失败");
        }
    }

    @Override
    public List<Farm> getActiveFarms() {
        log.debug("查询正常状态的农场列表");
        try {
            List<Farm> farms = farmMapper.selectActiveFarms();
            log.debug("查询到{}个正常状态的农场", farms.size());
            return farms;
        } catch (Exception e) {
            log.error("查询正常状态农场列表失败", e);
            throw new BusinessException("查询农场列表失败");
        }
    }

    @Override
    public IPage<Farm> getFarmPage(Page<Farm> page, Long ownerId, String name, 
                                  String province, String city, Integer status) {
        log.debug("分页查询农场列表: ownerId={}, name={}, province={}, city={}, status={}", 
                 ownerId, name, province, city, status);
        try {
            IPage<Farm> result = farmMapper.selectFarmPage(page, ownerId, name, province, city, status);
            log.debug("查询到{}条农场记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("分页查询农场列表失败", e);
            throw new BusinessException("查询农场列表失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createFarm(Farm farm) {
        if (farm == null) {
            throw new BusinessException("农场信息不能为空");
        }
        
        if (!StringUtils.hasText(farm.getName())) {
            throw new BusinessException("农场名称不能为空");
        }
        
        if (farm.getOwnerId() == null) {
            throw new BusinessException("农场主ID不能为空");
        }
        
        log.info("创建农场: {}", farm.getName());
        try {
            // 验证农场名称唯一性
            if (existsByName(farm.getName(), null)) {
                throw new BusinessException("农场名称已存在");
            }
            
            // 设置默认值
            if (farm.getStatus() == null) {
                farm.setStatus(2); // 默认为审核中状态
            }
            
            boolean result = save(farm);
            if (result) {
                log.info("农场创建成功，ID: {}", farm.getId());
            } else {
                log.error("农场创建失败");
                throw new BusinessException("农场创建失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建农场失败", e);
            throw new BusinessException("创建农场失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFarm(Farm farm) {
        if (farm == null || farm.getId() == null) {
            throw new BusinessException("农场信息不完整");
        }
        
        log.info("更新农场: ID={}, Name={}", farm.getId(), farm.getName());
        try {
            // 验证农场是否存在
            Farm existingFarm = getById(farm.getId());
            if (existingFarm == null) {
                throw new BusinessException("农场不存在");
            }
            
            // 验证农场名称唯一性
            if (StringUtils.hasText(farm.getName()) && existsByName(farm.getName(), farm.getId())) {
                throw new BusinessException("农场名称已存在");
            }
            
            boolean result = updateById(farm);
            if (result) {
                log.info("农场更新成功");
            } else {
                log.error("农场更新失败");
                throw new BusinessException("农场更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新农场失败", e);
            throw new BusinessException("更新农场失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFarm(Long farmId) {
        if (farmId == null) {
            throw new BusinessException("农场ID不能为空");
        }
        
        log.info("删除农场: ID={}", farmId);
        try {
            // 验证农场是否存在
            Farm farm = getById(farmId);
            if (farm == null) {
                throw new BusinessException("农场不存在");
            }
            
            // TODO: 检查是否有关联的地块、项目等业务数据
            
            boolean result = removeById(farmId);
            if (result) {
                log.info("农场删除成功");
            } else {
                log.error("农场删除失败");
                throw new BusinessException("农场删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除农场失败", e);
            throw new BusinessException("删除农场失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFarmStatus(Long farmId, Integer status) {
        if (farmId == null || status == null) {
            throw new BusinessException("参数不能为空");
        }
        
        if (status < 0 || status > 2) {
            throw new BusinessException("状态值无效");
        }
        
        log.info("更新农场状态: ID={}, Status={}", farmId, status);
        try {
            Farm farm = new Farm();
            farm.setId(farmId);
            farm.setStatus(status);
            
            boolean result = updateById(farm);
            if (result) {
                log.info("农场状态更新成功");
            } else {
                log.error("农场状态更新失败");
                throw new BusinessException("农场状态更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新农场状态失败", e);
            throw new BusinessException("更新农场状态失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditFarm(Long farmId, Boolean approved, String remark) {
        if (farmId == null || approved == null) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("审核农场: ID={}, Approved={}, Remark={}", farmId, approved, remark);
        try {
            // 验证农场是否存在
            Farm farm = getById(farmId);
            if (farm == null) {
                throw new BusinessException("农场不存在");
            }
            
            // 更新审核状态
            Integer newStatus = approved ? 1 : 0; // 1-正常，0-禁用
            boolean result = updateFarmStatus(farmId, newStatus);
            
            // TODO: 记录审核日志，发送通知等
            
            if (result) {
                log.info("农场审核完成，结果: {}", approved ? "通过" : "拒绝");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("审核农场失败", e);
            throw new BusinessException("审核农场失败");
        }
    }

    @Override
    public Farm getFarmDetail(Long farmId) {
        if (farmId == null) {
            throw new BusinessException("农场ID不能为空");
        }
        
        log.debug("获取农场详情: ID={}", farmId);
        try {
            Farm farm = getById(farmId);
            if (farm == null) {
                throw new BusinessException("农场不存在");
            }
            return farm;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取农场详情失败", e);
            throw new BusinessException("获取农场详情失败");
        }
    }

    @Override
    public boolean existsByName(String name, Long excludeId) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        
        LambdaQueryWrapper<Farm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Farm::getName, name);
        if (excludeId != null) {
            wrapper.ne(Farm::getId, excludeId);
        }
        
        return count(wrapper) > 0;
    }

    @Override
    public int countFarmsByOwnerId(Long ownerId) {
        if (ownerId == null) {
            return 0;
        }
        
        return farmMapper.countByOwnerId(ownerId);
    }

    @Override
    public List<Farm> getRecommendedFarms(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        log.debug("获取推荐农场列表，限制数量: {}", limit);
        try {
            // TODO: 根据评分、认养数量等因素排序
            LambdaQueryWrapper<Farm> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Farm::getStatus, 1);
            wrapper.orderByDesc(Farm::getCreateTime);
            wrapper.last("LIMIT " + limit);
            
            List<Farm> farms = list(wrapper);
            log.debug("获取到{}个推荐农场", farms.size());
            return farms;
        } catch (Exception e) {
            log.error("获取推荐农场列表失败", e);
            throw new BusinessException("获取推荐农场列表失败");
        }
    }

    @Override
    public IPage<Farm> searchFarms(String keyword, Page<Farm> page) {
        if (!StringUtils.hasText(keyword)) {
            throw new BusinessException("搜索关键词不能为空");
        }
        
        log.debug("搜索农场: keyword={}", keyword);
        try {
            LambdaQueryWrapper<Farm> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Farm::getStatus, 1);
            wrapper.and(w -> w.like(Farm::getName, keyword)
                    .or().like(Farm::getDescription, keyword)
                    .or().like(Farm::getAddress, keyword));
            wrapper.orderByDesc(Farm::getCreateTime);
            
            IPage<Farm> result = page(page, wrapper);
            log.debug("搜索到{}条农场记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("搜索农场失败", e);
            throw new BusinessException("搜索农场失败");
        }
    }

    @Override
    public Object getFarmStatistics(Long ownerId) {
        log.debug("获取农场统计信息，农场主ID: {}", ownerId);
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            if (ownerId != null) {
                // 特定农场主的统计
                LambdaQueryWrapper<Farm> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Farm::getOwnerId, ownerId);
                
                statistics.put("totalCount", count(wrapper));
                statistics.put("activeCount", count(wrapper.clone().eq(Farm::getStatus, 1)));
                statistics.put("pendingCount", count(wrapper.clone().eq(Farm::getStatus, 2)));
                statistics.put("disabledCount", count(wrapper.clone().eq(Farm::getStatus, 0)));
            } else {
                // 全局统计
                statistics.put("totalCount", count());
                statistics.put("activeCount", count(new LambdaQueryWrapper<Farm>().eq(Farm::getStatus, 1)));
                statistics.put("pendingCount", count(new LambdaQueryWrapper<Farm>().eq(Farm::getStatus, 2)));
                statistics.put("disabledCount", count(new LambdaQueryWrapper<Farm>().eq(Farm::getStatus, 0)));
            }
            
            return statistics;
        } catch (Exception e) {
            log.error("获取农场统计信息失败", e);
            throw new BusinessException("获取农场统计信息失败");
        }
    }

    @Override
    public boolean isFarmOwner(Long userId) {
        if (userId == null) {
            return false;
        }
        
        LambdaQueryWrapper<Farm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Farm::getOwnerId, userId);
        return count(wrapper) > 0;
    }

    @Override
    public List<Farm> getNearbyFarms(Double latitude, Double longitude, Double radius, Integer limit) {
        if (latitude == null || longitude == null) {
            throw new BusinessException("经纬度不能为空");
        }
        
        if (radius == null || radius <= 0) {
            radius = 10.0; // 默认10公里
        }
        
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        
        log.debug("获取附近农场: lat={}, lng={}, radius={}km, limit={}", latitude, longitude, radius, limit);
        try {
            // TODO: 实现基于地理位置的查询
            // 这里简化处理，返回正常状态的农场
            LambdaQueryWrapper<Farm> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Farm::getStatus, 1);
            wrapper.isNotNull(Farm::getLatitude);
            wrapper.isNotNull(Farm::getLongitude);
            wrapper.orderByDesc(Farm::getCreateTime);
            wrapper.last("LIMIT " + limit);
            
            List<Farm> farms = list(wrapper);
            log.debug("获取到{}个附近农场", farms.size());
            return farms;
        } catch (Exception e) {
            log.error("获取附近农场失败", e);
            throw new BusinessException("获取附近农场失败");
        }
    }
}
