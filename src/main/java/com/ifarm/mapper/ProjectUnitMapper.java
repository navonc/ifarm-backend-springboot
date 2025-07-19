package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifarm.entity.ProjectUnit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目单元Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface ProjectUnitMapper extends BaseMapper<ProjectUnit> {

    /**
     * 根据项目ID查询单元列表
     * 
     * @param projectId 项目ID
     * @return 单元列表
     */
    List<ProjectUnit> selectByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据项目ID查询可认养单元列表
     * 
     * @param projectId 项目ID
     * @return 可认养单元列表
     */
    List<ProjectUnit> selectAvailableByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据项目ID和单元状态查询单元列表
     * 
     * @param projectId 项目ID
     * @param unitStatus 单元状态
     * @return 单元列表
     */
    List<ProjectUnit> selectByProjectIdAndStatus(@Param("projectId") Long projectId, 
                                                 @Param("unitStatus") Integer unitStatus);

    /**
     * 统计项目的单元数量
     * 
     * @param projectId 项目ID
     * @return 单元数量
     */
    int countByProjectId(@Param("projectId") Long projectId);

    /**
     * 统计项目指定状态的单元数量
     * 
     * @param projectId 项目ID
     * @param unitStatus 单元状态
     * @return 单元数量
     */
    int countByProjectIdAndStatus(@Param("projectId") Long projectId, 
                                  @Param("unitStatus") Integer unitStatus);

    /**
     * 批量更新单元状态
     * 
     * @param unitIds 单元ID列表
     * @param unitStatus 新状态
     * @return 更新行数
     */
    int batchUpdateStatus(@Param("unitIds") List<Long> unitIds, 
                          @Param("unitStatus") Integer unitStatus);

    /**
     * 根据项目ID和单元编号查询单元
     * 
     * @param projectId 项目ID
     * @param unitNumber 单元编号
     * @return 单元信息
     */
    ProjectUnit selectByProjectIdAndUnitNumber(@Param("projectId") Long projectId, 
                                               @Param("unitNumber") String unitNumber);
}
