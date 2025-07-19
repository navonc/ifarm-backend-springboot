package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.entity.MediaFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 媒体文件Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface MediaFileMapper extends BaseMapper<MediaFile> {

    /**
     * 根据关联类型和关联ID查询媒体文件列表
     * 
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @return 媒体文件列表
     */
    List<MediaFile> selectByRelated(@Param("relatedType") String relatedType, 
                                    @Param("relatedId") Long relatedId);

    /**
     * 根据上传者ID查询媒体文件列表
     * 
     * @param uploaderId 上传者ID
     * @return 媒体文件列表
     */
    List<MediaFile> selectByUploaderId(@Param("uploaderId") Long uploaderId);

    /**
     * 根据文件类型查询媒体文件列表
     * 
     * @param fileType 文件类型
     * @return 媒体文件列表
     */
    List<MediaFile> selectByFileType(@Param("fileType") String fileType);

    /**
     * 根据文件路径查询媒体文件
     * 
     * @param filePath 文件路径
     * @return 媒体文件
     */
    MediaFile selectByFilePath(@Param("filePath") String filePath);

    /**
     * 分页查询媒体文件
     * 
     * @param page 分页参数
     * @param fileType 文件类型（可选）
     * @param relatedType 关联类型（可选）
     * @param uploaderId 上传者ID（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<MediaFile> selectMediaFilePage(Page<MediaFile> page,
                                         @Param("fileType") String fileType,
                                         @Param("relatedType") String relatedType,
                                         @Param("uploaderId") Long uploaderId,
                                         @Param("status") Integer status);

    /**
     * 根据MIME类型查询媒体文件列表
     * 
     * @param mimeType MIME类型
     * @return 媒体文件列表
     */
    List<MediaFile> selectByMimeType(@Param("mimeType") String mimeType);

    /**
     * 统计用户上传的文件数量
     * 
     * @param uploaderId 上传者ID
     * @param fileType 文件类型（可选）
     * @return 文件数量
     */
    int countByUploader(@Param("uploaderId") Long uploaderId, @Param("fileType") String fileType);

    /**
     * 统计关联对象的媒体文件数量
     * 
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @return 文件数量
     */
    int countByRelated(@Param("relatedType") String relatedType, @Param("relatedId") Long relatedId);

    /**
     * 查询正常状态的媒体文件列表
     * 
     * @return 正常状态的媒体文件列表
     */
    List<MediaFile> selectActiveFiles();

    /**
     * 批量更新文件状态
     * 
     * @param fileIds 文件ID列表
     * @param status 新状态
     * @return 更新行数
     */
    int batchUpdateStatus(@Param("fileIds") List<Long> fileIds, @Param("status") Integer status);
}
