package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.Category;
import com.ifarm.mapper.CategoryMapper;
import com.ifarm.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> getChildrenByParentId(Long parentId) {
        log.debug("查询父分类ID为{}的子分类列表", parentId);
        try {
            List<Category> children = categoryMapper.selectByParentId(parentId);
            log.debug("查询到{}个子分类", children.size());
            return children;
        } catch (Exception e) {
            log.error("查询子分类列表失败，父分类ID: {}", parentId, e);
            throw new BusinessException("查询子分类列表失败");
        }
    }

    @Override
    public Category getCategoryByCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new BusinessException("分类编码不能为空");
        }
        
        log.debug("根据编码查询分类: {}", code);
        try {
            Category category = categoryMapper.selectByCode(code);
            if (category == null) {
                log.warn("未找到编码为{}的分类", code);
            }
            return category;
        } catch (Exception e) {
            log.error("根据编码查询分类失败，编码: {}", code, e);
            throw new BusinessException("查询分类失败");
        }
    }

    @Override
    public List<Category> getEnabledCategories() {
        log.debug("查询启用状态的分类列表");
        try {
            List<Category> categories = categoryMapper.selectEnabledCategories();
            log.debug("查询到{}个启用的分类", categories.size());
            return categories;
        } catch (Exception e) {
            log.error("查询启用分类列表失败", e);
            throw new BusinessException("查询分类列表失败");
        }
    }

    @Override
    public List<Category> getTopCategories() {
        log.debug("查询顶级分类列表");
        return getChildrenByParentId(0L);
    }

    @Override
    public List<Category> buildCategoryTree() {
        log.debug("构建分类树结构");
        try {
            // 查询所有启用的分类
            List<Category> allCategories = getEnabledCategories();
            
            // 按父分类ID分组
            Map<Long, List<Category>> categoryMap = allCategories.stream()
                    .collect(Collectors.groupingBy(Category::getParentId));
            
            // 构建树结构
            List<Category> rootCategories = categoryMap.getOrDefault(0L, new ArrayList<>());
            buildTree(rootCategories, categoryMap);
            
            log.debug("构建分类树完成，根节点数量: {}", rootCategories.size());
            return rootCategories;
        } catch (Exception e) {
            log.error("构建分类树失败", e);
            throw new BusinessException("构建分类树失败");
        }
    }

    /**
     * 递归构建分类树
     */
    private void buildTree(List<Category> categories, Map<Long, List<Category>> categoryMap) {
        for (Category category : categories) {
            List<Category> children = categoryMap.getOrDefault(category.getId(), new ArrayList<>());
            if (!children.isEmpty()) {
                buildTree(children, categoryMap);
                // 这里可以设置children字段，如果Category实体有children字段的话
                // category.setChildren(children);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCategory(Category category) {
        if (category == null) {
            throw new BusinessException("分类信息不能为空");
        }
        
        log.info("创建分类: {}", category.getName());
        try {
            // 验证分类编码唯一性
            if (StringUtils.hasText(category.getCode()) && existsByCode(category.getCode(), null)) {
                throw new BusinessException("分类编码已存在");
            }
            
            // 设置默认值
            if (category.getStatus() == null) {
                category.setStatus(1);
            }
            if (category.getSortOrder() == null) {
                category.setSortOrder(0);
            }
            if (category.getParentId() == null) {
                category.setParentId(0L);
            }
            
            boolean result = save(category);
            if (result) {
                log.info("分类创建成功，ID: {}", category.getId());
            } else {
                log.error("分类创建失败");
                throw new BusinessException("分类创建失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建分类失败", e);
            throw new BusinessException("创建分类失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(Category category) {
        if (category == null || category.getId() == null) {
            throw new BusinessException("分类信息不完整");
        }
        
        log.info("更新分类: ID={}, Name={}", category.getId(), category.getName());
        try {
            // 验证分类是否存在
            Category existingCategory = getById(category.getId());
            if (existingCategory == null) {
                throw new BusinessException("分类不存在");
            }
            
            // 验证分类编码唯一性
            if (StringUtils.hasText(category.getCode()) && existsByCode(category.getCode(), category.getId())) {
                throw new BusinessException("分类编码已存在");
            }
            
            boolean result = updateById(category);
            if (result) {
                log.info("分类更新成功");
            } else {
                log.error("分类更新失败");
                throw new BusinessException("分类更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新分类失败", e);
            throw new BusinessException("更新分类失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException("分类ID不能为空");
        }
        
        log.info("删除分类: ID={}", categoryId);
        try {
            // 验证分类是否存在
            Category category = getById(categoryId);
            if (category == null) {
                throw new BusinessException("分类不存在");
            }
            
            // 检查是否有子分类
            if (hasChildren(categoryId)) {
                throw new BusinessException("该分类下存在子分类，无法删除");
            }
            
            // TODO: 检查是否有关联的作物等业务数据
            
            boolean result = removeById(categoryId);
            if (result) {
                log.info("分类删除成功");
            } else {
                log.error("分类删除失败");
                throw new BusinessException("分类删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除分类失败", e);
            throw new BusinessException("删除分类失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategoryStatus(Long categoryId, Integer status) {
        if (categoryId == null || status == null) {
            throw new BusinessException("参数不能为空");
        }
        
        if (status < 0 || status > 1) {
            throw new BusinessException("状态值无效");
        }
        
        log.info("更新分类状态: ID={}, Status={}", categoryId, status);
        try {
            Category category = new Category();
            category.setId(categoryId);
            category.setStatus(status);
            
            boolean result = updateById(category);
            if (result) {
                log.info("分类状态更新成功");
            } else {
                log.error("分类状态更新失败");
                throw new BusinessException("分类状态更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新分类状态失败", e);
            throw new BusinessException("更新分类状态失败");
        }
    }

    @Override
    public boolean existsByCode(String code, Long excludeId) {
        if (!StringUtils.hasText(code)) {
            return false;
        }
        
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getCode, code);
        if (excludeId != null) {
            wrapper.ne(Category::getId, excludeId);
        }
        
        return count(wrapper) > 0;
    }

    @Override
    public boolean hasChildren(Long categoryId) {
        if (categoryId == null) {
            return false;
        }
        
        return categoryMapper.countByParentId(categoryId) > 0;
    }

    @Override
    public String getCategoryPath(Long categoryId) {
        if (categoryId == null) {
            return "";
        }
        
        log.debug("获取分类路径: ID={}", categoryId);
        try {
            List<String> pathList = new ArrayList<>();
            Category current = getById(categoryId);
            
            while (current != null && current.getParentId() != 0) {
                pathList.add(0, current.getName());
                current = getById(current.getParentId());
            }
            
            if (current != null) {
                pathList.add(0, current.getName());
            }
            
            String path = String.join(" > ", pathList);
            log.debug("分类路径: {}", path);
            return path;
        } catch (Exception e) {
            log.error("获取分类路径失败", e);
            return "";
        }
    }
}
