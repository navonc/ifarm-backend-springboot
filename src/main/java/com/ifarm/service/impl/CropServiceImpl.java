package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.Crop;
import com.ifarm.mapper.CropMapper;
import com.ifarm.service.ICropService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作物品种服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CropServiceImpl extends ServiceImpl<CropMapper, Crop> implements ICropService {

    private final CropMapper cropMapper;

    @Override
    public List<Crop> getCropsByCategoryId(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException("分类ID不能为空");
        }
        
        log.debug("根据分类ID查询作物列表: {}", categoryId);
        try {
            List<Crop> crops = cropMapper.selectByCategoryId(categoryId);
            log.debug("查询到{}个作物", crops.size());
            return crops;
        } catch (Exception e) {
            log.error("根据分类ID查询作物列表失败，分类ID: {}", categoryId, e);
            throw new BusinessException("查询作物列表失败");
        }
    }

    @Override
    public List<Crop> getEnabledCrops() {
        log.debug("查询启用状态的作物列表");
        try {
            List<Crop> crops = cropMapper.selectEnabledCrops();
            log.debug("查询到{}个启用的作物", crops.size());
            return crops;
        } catch (Exception e) {
            log.error("查询启用作物列表失败", e);
            throw new BusinessException("查询作物列表失败");
        }
    }

    @Override
    public IPage<Crop> getCropPage(Page<Crop> page, Long categoryId, String name, Integer status) {
        log.debug("分页查询作物列表: categoryId={}, name={}, status={}", categoryId, name, status);
        try {
            IPage<Crop> result = cropMapper.selectCropPage(page, categoryId, name, status);
            log.debug("查询到{}条作物记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("分页查询作物列表失败", e);
            throw new BusinessException("查询作物列表失败");
        }
    }

    @Override
    public List<Crop> getCropsByPlantingSeason(String plantingSeason) {
        if (!StringUtils.hasText(plantingSeason)) {
            throw new BusinessException("种植季节不能为空");
        }
        
        log.debug("根据种植季节查询作物列表: {}", plantingSeason);
        try {
            List<Crop> crops = cropMapper.selectByPlantingSeason(plantingSeason);
            log.debug("查询到{}个{}季节的作物", crops.size(), plantingSeason);
            return crops;
        } catch (Exception e) {
            log.error("根据种植季节查询作物列表失败，季节: {}", plantingSeason, e);
            throw new BusinessException("查询作物列表失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCrop(Crop crop) {
        if (crop == null) {
            throw new BusinessException("作物信息不能为空");
        }
        
        if (!StringUtils.hasText(crop.getName())) {
            throw new BusinessException("作物名称不能为空");
        }
        
        log.info("创建作物: {}", crop.getName());
        try {
            // 验证作物名称唯一性
            if (existsByName(crop.getName(), null)) {
                throw new BusinessException("作物名称已存在");
            }
            
            // 设置默认值
            if (crop.getStatus() == null) {
                crop.setStatus(1);
            }
            
            boolean result = save(crop);
            if (result) {
                log.info("作物创建成功，ID: {}", crop.getId());
            } else {
                log.error("作物创建失败");
                throw new BusinessException("作物创建失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建作物失败", e);
            throw new BusinessException("创建作物失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCrop(Crop crop) {
        if (crop == null || crop.getId() == null) {
            throw new BusinessException("作物信息不完整");
        }
        
        log.info("更新作物: ID={}, Name={}", crop.getId(), crop.getName());
        try {
            // 验证作物是否存在
            Crop existingCrop = getById(crop.getId());
            if (existingCrop == null) {
                throw new BusinessException("作物不存在");
            }
            
            // 验证作物名称唯一性
            if (StringUtils.hasText(crop.getName()) && existsByName(crop.getName(), crop.getId())) {
                throw new BusinessException("作物名称已存在");
            }
            
            boolean result = updateById(crop);
            if (result) {
                log.info("作物更新成功");
            } else {
                log.error("作物更新失败");
                throw new BusinessException("作物更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新作物失败", e);
            throw new BusinessException("更新作物失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCrop(Long cropId) {
        if (cropId == null) {
            throw new BusinessException("作物ID不能为空");
        }
        
        log.info("删除作物: ID={}", cropId);
        try {
            // 验证作物是否存在
            Crop crop = getById(cropId);
            if (crop == null) {
                throw new BusinessException("作物不存在");
            }
            
            // TODO: 检查是否有关联的认养项目等业务数据
            
            boolean result = removeById(cropId);
            if (result) {
                log.info("作物删除成功");
            } else {
                log.error("作物删除失败");
                throw new BusinessException("作物删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除作物失败", e);
            throw new BusinessException("删除作物失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCropStatus(Long cropId, Integer status) {
        if (cropId == null || status == null) {
            throw new BusinessException("参数不能为空");
        }
        
        if (status < 0 || status > 1) {
            throw new BusinessException("状态值无效");
        }
        
        log.info("更新作物状态: ID={}, Status={}", cropId, status);
        try {
            Crop crop = new Crop();
            crop.setId(cropId);
            crop.setStatus(status);
            
            boolean result = updateById(crop);
            if (result) {
                log.info("作物状态更新成功");
            } else {
                log.error("作物状态更新失败");
                throw new BusinessException("作物状态更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新作物状态失败", e);
            throw new BusinessException("更新作物状态失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateCropStatus(List<Long> cropIds, Integer status) {
        if (cropIds == null || cropIds.isEmpty()) {
            throw new BusinessException("作物ID列表不能为空");
        }
        
        if (status == null || status < 0 || status > 1) {
            throw new BusinessException("状态值无效");
        }
        
        log.info("批量更新作物状态: IDs={}, Status={}", cropIds, status);
        try {
            LambdaQueryWrapper<Crop> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Crop::getId, cropIds);
            
            Crop crop = new Crop();
            crop.setStatus(status);
            
            boolean result = update(crop, wrapper);
            if (result) {
                log.info("批量更新作物状态成功，影响{}条记录", cropIds.size());
            } else {
                log.error("批量更新作物状态失败");
                throw new BusinessException("批量更新作物状态失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量更新作物状态失败", e);
            throw new BusinessException("批量更新作物状态失败");
        }
    }

    @Override
    public Crop getCropDetail(Long cropId) {
        if (cropId == null) {
            throw new BusinessException("作物ID不能为空");
        }
        
        log.debug("获取作物详情: ID={}", cropId);
        try {
            Crop crop = getById(cropId);
            if (crop == null) {
                throw new BusinessException("作物不存在");
            }
            return crop;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取作物详情失败", e);
            throw new BusinessException("获取作物详情失败");
        }
    }

    @Override
    public boolean existsByName(String name, Long excludeId) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        
        LambdaQueryWrapper<Crop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Crop::getName, name);
        if (excludeId != null) {
            wrapper.ne(Crop::getId, excludeId);
        }
        
        return count(wrapper) > 0;
    }

    @Override
    public List<Crop> getPopularCrops(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        log.debug("获取热门作物列表，限制数量: {}", limit);
        try {
            // TODO: 根据认养数量、评分等因素排序
            LambdaQueryWrapper<Crop> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Crop::getStatus, 1);
            wrapper.orderByDesc(Crop::getCreateTime);
            wrapper.last("LIMIT " + limit);
            
            List<Crop> crops = list(wrapper);
            log.debug("获取到{}个热门作物", crops.size());
            return crops;
        } catch (Exception e) {
            log.error("获取热门作物列表失败", e);
            throw new BusinessException("获取热门作物列表失败");
        }
    }

    @Override
    public List<Crop> getRecommendedCrops(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        log.debug("获取推荐作物列表，限制数量: {}", limit);
        try {
            // 根据当前季节推荐作物
            String currentSeason = getCurrentSeason();
            List<Crop> seasonCrops = getCropsByPlantingSeason(currentSeason);
            
            if (seasonCrops.size() > limit) {
                return seasonCrops.subList(0, limit);
            }
            
            return seasonCrops;
        } catch (Exception e) {
            log.error("获取推荐作物列表失败", e);
            throw new BusinessException("获取推荐作物列表失败");
        }
    }

    @Override
    public IPage<Crop> searchCrops(String keyword, Page<Crop> page) {
        if (!StringUtils.hasText(keyword)) {
            throw new BusinessException("搜索关键词不能为空");
        }
        
        log.debug("搜索作物: keyword={}", keyword);
        try {
            LambdaQueryWrapper<Crop> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Crop::getStatus, 1);
            wrapper.and(w -> w.like(Crop::getName, keyword)
                    .or().like(Crop::getVariety, keyword)
                    .or().like(Crop::getDescription, keyword));
            wrapper.orderByDesc(Crop::getCreateTime);
            
            IPage<Crop> result = page(page, wrapper);
            log.debug("搜索到{}条作物记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("搜索作物失败", e);
            throw new BusinessException("搜索作物失败");
        }
    }

    @Override
    public Object getCropStatistics() {
        log.debug("获取作物统计信息");
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总数统计
            statistics.put("totalCount", count());
            statistics.put("enabledCount", count(new LambdaQueryWrapper<Crop>().eq(Crop::getStatus, 1)));
            statistics.put("disabledCount", count(new LambdaQueryWrapper<Crop>().eq(Crop::getStatus, 0)));
            
            // TODO: 添加更多统计信息，如各分类数量、热门作物等
            
            return statistics;
        } catch (Exception e) {
            log.error("获取作物统计信息失败", e);
            throw new BusinessException("获取作物统计信息失败");
        }
    }

    /**
     * 获取当前季节
     */
    private String getCurrentSeason() {
        Month currentMonth = LocalDate.now().getMonth();
        
        if (currentMonth == Month.MARCH || currentMonth == Month.APRIL || currentMonth == Month.MAY) {
            return "春季";
        } else if (currentMonth == Month.JUNE || currentMonth == Month.JULY || currentMonth == Month.AUGUST) {
            return "夏季";
        } else if (currentMonth == Month.SEPTEMBER || currentMonth == Month.OCTOBER || currentMonth == Month.NOVEMBER) {
            return "秋季";
        } else {
            return "冬季";
        }
    }
}
