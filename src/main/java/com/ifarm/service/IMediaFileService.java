package com.ifarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.MediaFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 媒体文件服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IMediaFileService extends IService<MediaFile> {

    /**
     * 根据关联类型和关联ID查询媒体文件列表
     * 
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @return 媒体文件列表
     */
    List<MediaFile> getFilesByRelated(String relatedType, Long relatedId);

    /**
     * 根据上传者ID查询媒体文件列表
     * 
     * @param uploaderId 上传者ID
     * @return 媒体文件列表
     */
    List<MediaFile> getFilesByUploaderId(Long uploaderId);

    /**
     * 根据文件类型查询媒体文件列表
     * 
     * @param fileType 文件类型
     * @return 媒体文件列表
     */
    List<MediaFile> getFilesByFileType(String fileType);

    /**
     * 根据文件路径查询媒体文件
     * 
     * @param filePath 文件路径
     * @return 媒体文件
     */
    MediaFile getFileByFilePath(String filePath);

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
    IPage<MediaFile> getMediaFilePage(Page<MediaFile> page, String fileType, String relatedType, 
                                     Long uploaderId, Integer status);

    /**
     * 上传单个文件
     * 
     * @param file 文件
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @param uploaderId 上传者ID
     * @return 媒体文件信息
     */
    MediaFile uploadFile(MultipartFile file, String relatedType, Long relatedId, Long uploaderId);

    /**
     * 批量上传文件
     * 
     * @param files 文件列表
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @param uploaderId 上传者ID
     * @return 媒体文件列表
     */
    List<MediaFile> batchUploadFiles(List<MultipartFile> files, String relatedType, 
                                    Long relatedId, Long uploaderId);

    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean deleteFile(Long fileId, Long userId);

    /**
     * 批量删除文件
     * 
     * @param fileIds 文件ID列表
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean batchDeleteFiles(List<Long> fileIds, Long userId);

    /**
     * 更新文件信息
     * 
     * @param mediaFile 媒体文件
     * @return 更新结果
     */
    boolean updateFile(MediaFile mediaFile);

    /**
     * 更新文件状态
     * 
     * @param fileId 文件ID
     * @param status 状态
     * @return 更新结果
     */
    boolean updateFileStatus(Long fileId, Integer status);

    /**
     * 批量更新文件状态
     * 
     * @param fileIds 文件ID列表
     * @param status 新状态
     * @return 更新结果
     */
    boolean batchUpdateFileStatus(List<Long> fileIds, Integer status);

    /**
     * 获取文件详情
     * 
     * @param fileId 文件ID
     * @return 文件详情
     */
    MediaFile getFileDetail(Long fileId);

    /**
     * 检查用户是否有权限操作文件
     * 
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, Long fileId);

    /**
     * 生成文件访问URL
     * 
     * @param fileId 文件ID
     * @return 访问URL
     */
    String generateFileUrl(Long fileId);

    /**
     * 生成文件缩略图
     * 
     * @param fileId 文件ID
     * @param width 宽度
     * @param height 高度
     * @return 缩略图URL
     */
    String generateThumbnail(Long fileId, Integer width, Integer height);

    /**
     * 统计用户上传的文件数量
     * 
     * @param uploaderId 上传者ID
     * @param fileType 文件类型（可选）
     * @return 文件数量
     */
    int countFilesByUploader(Long uploaderId, String fileType);

    /**
     * 统计关联对象的媒体文件数量
     * 
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @return 文件数量
     */
    int countFilesByRelated(String relatedType, Long relatedId);

    /**
     * 获取文件存储统计信息
     * 
     * @param uploaderId 上传者ID（可选）
     * @return 存储统计信息
     */
    Object getStorageStatistics(Long uploaderId);

    /**
     * 清理无效文件
     * 
     * @return 清理的文件数量
     */
    int cleanupInvalidFiles();

    /**
     * 检查文件是否存在
     * 
     * @param filePath 文件路径
     * @return 是否存在
     */
    boolean fileExists(String filePath);

    /**
     * 获取支持的文件类型列表
     * 
     * @return 文件类型列表
     */
    List<String> getSupportedFileTypes();

    /**
     * 验证文件类型
     * 
     * @param file 文件
     * @param allowedTypes 允许的类型列表
     * @return 验证结果
     */
    boolean validateFileType(MultipartFile file, List<String> allowedTypes);

    /**
     * 验证文件大小
     * 
     * @param file 文件
     * @param maxSize 最大大小（字节）
     * @return 验证结果
     */
    boolean validateFileSize(MultipartFile file, Long maxSize);

    /**
     * 获取文件上传配置
     * 
     * @return 上传配置
     */
    Object getUploadConfig();

    /**
     * 压缩图片文件
     * 
     * @param fileId 文件ID
     * @param quality 压缩质量（0-100）
     * @return 压缩结果
     */
    boolean compressImage(Long fileId, Integer quality);

    /**
     * 转换文件格式
     * 
     * @param fileId 文件ID
     * @param targetFormat 目标格式
     * @return 转换结果
     */
    MediaFile convertFileFormat(Long fileId, String targetFormat);
}
