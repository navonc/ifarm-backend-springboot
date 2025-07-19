package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.entity.AdoptionProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 认养项目Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface AdoptionProjectMapper extends BaseMapper<AdoptionProject> {

    /**
     * 根据地块ID查询项目列表
     * 
     * @param plotId 地块ID
     * @return 项目列表
     */
    List<AdoptionProject> selectByPlotId(@Param("plotId") Long plotId);

    /**
     * 根据作物ID查询项目列表
     * 
     * @param cropId 作物ID
     * @return 项目列表
     */
    List<AdoptionProject> selectByCropId(@Param("cropId") Long cropId);

    /**
     * 查询可认养的项目列表
     * 
     * @return 可认养的项目列表
     */
    List<AdoptionProject> selectAvailableProjects();

    /**
     * 分页查询认养项目列表
     * 
     * @param page 分页参数
     * @param cropId 作物ID（可选）
     * @param projectStatus 项目状态（可选）
     * @param name 项目名称（可选，模糊查询）
     * @return 分页结果
     */
    IPage<AdoptionProject> selectProjectPage(Page<AdoptionProject> page,
                                             @Param("cropId") Long cropId,
                                             @Param("projectStatus") Integer projectStatus,
                                             @Param("name") String name);

    /**
     * 根据项目状态查询项目列表
     * 
     * @param projectStatus 项目状态
     * @return 项目列表
     */
    List<AdoptionProject> selectByProjectStatus(@Param("projectStatus") Integer projectStatus);

    /**
     * 更新项目可用单元数
     * 
     * @param projectId 项目ID
     * @param unitCount 单元数量（正数增加，负数减少）
     * @return 更新行数
     */
    int updateAvailableUnits(@Param("projectId") Long projectId, @Param("unitCount") Integer unitCount);

    /**
     * 查询项目详情（包含农场和作物信息）
     * 
     * @param projectId 项目ID
     * @return 项目详情
     */
    AdoptionProject selectProjectDetail(@Param("projectId") Long projectId);
}
