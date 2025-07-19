package com.ifarm.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean转换工具类
 * 基于Hutool的BeanUtil进行封装，提供更便捷的对象转换功能
 * 
 * @author ifarm
 * @since 2025-7-18
 */
@Slf4j
public class BeanUtils {

    /**
     * 私有构造函数，防止实例化
     */
    private BeanUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 单个对象转换
     * 将源对象转换为目标类型的对象
     * 
     * @param source 源对象
     * @param targetClass 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的目标对象，如果源对象为null则返回null
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null) {
            log.debug("源对象为null，返回null");
            return null;
        }
        
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtil.copyProperties(source, target);
            log.debug("对象转换成功: {} -> {}", source.getClass().getSimpleName(), targetClass.getSimpleName());
            return target;
        } catch (Exception e) {
            log.error("对象转换失败: {} -> {}, 错误信息: {}", 
                    source.getClass().getSimpleName(), targetClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("对象转换失败", e);
        }
    }

    /**
     * 单个对象转换（忽略null值）
     * 将源对象转换为目标类型的对象，忽略源对象中的null值属性
     * 
     * @param source 源对象
     * @param targetClass 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的目标对象，如果源对象为null则返回null
     */
    public static <T> T copyPropertiesIgnoreNull(Object source, Class<T> targetClass) {
        if (source == null) {
            log.debug("源对象为null，返回null");
            return null;
        }
        
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtil.copyProperties(source, target, true);
            log.debug("对象转换成功（忽略null值）: {} -> {}", source.getClass().getSimpleName(), targetClass.getSimpleName());
            return target;
        } catch (Exception e) {
            log.error("对象转换失败（忽略null值）: {} -> {}, 错误信息: {}", 
                    source.getClass().getSimpleName(), targetClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("对象转换失败", e);
        }
    }

    /**
     * 对象属性复制到已存在的目标对象
     * 
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            log.warn("源对象或目标对象为null，跳过属性复制");
            return;
        }
        
        try {
            BeanUtil.copyProperties(source, target);
            log.debug("属性复制成功: {} -> {}", source.getClass().getSimpleName(), target.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("属性复制失败: {} -> {}, 错误信息: {}", 
                    source.getClass().getSimpleName(), target.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("属性复制失败", e);
        }
    }

    /**
     * 对象属性复制到已存在的目标对象（忽略null值）
     * 
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        if (source == null || target == null) {
            log.warn("源对象或目标对象为null，跳过属性复制");
            return;
        }
        
        try {
            BeanUtil.copyProperties(source, target, true);
            log.debug("属性复制成功（忽略null值）: {} -> {}", source.getClass().getSimpleName(), target.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("属性复制失败（忽略null值）: {} -> {}, 错误信息: {}", 
                    source.getClass().getSimpleName(), target.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("属性复制失败", e);
        }
    }

    /**
     * 集合对象转换
     * 将源对象集合转换为目标类型的对象集合
     * 
     * @param sourceList 源对象集合
     * @param targetClass 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的目标对象集合，如果源集合为null或空则返回空集合
     */
    public static <T> List<T> copyPropertiesList(List<?> sourceList, Class<T> targetClass) {
        if (CollUtil.isEmpty(sourceList)) {
            log.debug("源集合为空，返回空集合");
            return Collections.emptyList();
        }
        
        try {
            List<T> targetList = sourceList.stream()
                    .map(source -> copyProperties(source, targetClass))
                    .collect(Collectors.toList());
            log.debug("集合转换成功: {} 个对象从 {} 转换为 {}", 
                    sourceList.size(), 
                    sourceList.get(0).getClass().getSimpleName(), 
                    targetClass.getSimpleName());
            return targetList;
        } catch (Exception e) {
            log.error("集合转换失败: 错误信息: {}", e.getMessage(), e);
            throw new RuntimeException("集合转换失败", e);
        }
    }

    /**
     * 集合对象转换（忽略null值）
     * 将源对象集合转换为目标类型的对象集合，忽略源对象中的null值属性
     * 
     * @param sourceList 源对象集合
     * @param targetClass 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的目标对象集合，如果源集合为null或空则返回空集合
     */
    public static <T> List<T> copyPropertiesListIgnoreNull(List<?> sourceList, Class<T> targetClass) {
        if (CollUtil.isEmpty(sourceList)) {
            log.debug("源集合为空，返回空集合");
            return Collections.emptyList();
        }
        
        try {
            List<T> targetList = sourceList.stream()
                    .map(source -> copyPropertiesIgnoreNull(source, targetClass))
                    .collect(Collectors.toList());
            log.debug("集合转换成功（忽略null值）: {} 个对象从 {} 转换为 {}", 
                    sourceList.size(), 
                    sourceList.get(0).getClass().getSimpleName(), 
                    targetClass.getSimpleName());
            return targetList;
        } catch (Exception e) {
            log.error("集合转换失败（忽略null值）: 错误信息: {}", e.getMessage(), e);
            throw new RuntimeException("集合转换失败", e);
        }
    }
}
