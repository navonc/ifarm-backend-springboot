package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.MediaFile;
import com.ifarm.mapper.MediaFileMapper;
import com.ifarm.service.IMediaFileService;
import com.ifarm.service.ISystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 媒体文件服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaFileServiceImpl extends ServiceImpl<MediaFileMapper, MediaFile> implements IMediaFileService {

    private final MediaFileMapper mediaFileMapper;
    private final ISystemConfigService systemConfigService;

    @Override
    public List<MediaFile> getFilesByRelated(String relatedType, Long relatedId) {
        if (!StringUtils.hasText(relatedType) || relatedId == null) {
            throw new BusinessException("关联类型和关联ID不能为空");
        }
        
        log.debug("根据关联类型和关联ID查询媒体文件列表: relatedType={}, relatedId={}", relatedType, relatedId);
        try {
            List<MediaFile> files = mediaFileMapper.selectByRelated(relatedType, relatedId);
            log.debug("查询到{}个媒体文件", files.size());
            return files;
        } catch (Exception e) {
            log.error("根据关联类型和关联ID查询媒体文件列表失败", e);
            throw new BusinessException("查询媒体文件列表失败");
        }
    }

    @Override
    public List<MediaFile> getFilesByUploaderId(Long uploaderId) {
        if (uploaderId == null) {
            throw new BusinessException("上传者ID不能为空");
        }
        
        log.debug("根据上传者ID查询媒体文件列表: {}", uploaderId);
        try {
            List<MediaFile> files = mediaFileMapper.selectByUploaderId(uploaderId);
            log.debug("查询到{}个媒体文件", files.size());
            return files;
        } catch (Exception e) {
            log.error("根据上传者ID查询媒体文件列表失败，上传者ID: {}", uploaderId, e);
            throw new BusinessException("查询媒体文件列表失败");
        }
    }

    @Override
    public List<MediaFile> getFilesByFileType(String fileType) {
        if (!StringUtils.hasText(fileType)) {
            throw new BusinessException("文件类型不能为空");
        }
        
        log.debug("根据文件类型查询媒体文件列表: {}", fileType);
        try {
            List<MediaFile> files = mediaFileMapper.selectByFileType(fileType);
            log.debug("查询到{}个{}类型的媒体文件", files.size(), fileType);
            return files;
        } catch (Exception e) {
            log.error("根据文件类型查询媒体文件列表失败", e);
            throw new BusinessException("查询媒体文件列表失败");
        }
    }

    @Override
    public MediaFile getFileByFilePath(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            throw new BusinessException("文件路径不能为空");
        }
        
        log.debug("根据文件路径查询媒体文件: {}", filePath);
        try {
            return mediaFileMapper.selectByFilePath(filePath);
        } catch (Exception e) {
            log.error("根据文件路径查询媒体文件失败", e);
            throw new BusinessException("查询媒体文件失败");
        }
    }

    @Override
    public IPage<MediaFile> getMediaFilePage(Page<MediaFile> page, String fileType, String relatedType, 
                                           Long uploaderId, Integer status) {
        log.debug("分页查询媒体文件: fileType={}, relatedType={}, uploaderId={}, status={}", 
                fileType, relatedType, uploaderId, status);
        try {
            IPage<MediaFile> result = mediaFileMapper.selectMediaFilePage(page, fileType, relatedType, uploaderId, status);
            log.debug("查询到{}个媒体文件", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("分页查询媒体文件失败", e);
            throw new BusinessException("查询媒体文件失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaFile uploadFile(MultipartFile file, String relatedType, Long relatedId, Long uploaderId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        
        if (!StringUtils.hasText(relatedType) || relatedId == null || uploaderId == null) {
            throw new BusinessException("关联类型、关联ID和上传者ID不能为空");
        }
        
        log.info("上传文件: fileName={}, size={}, relatedType={}, relatedId={}, uploaderId={}", 
                file.getOriginalFilename(), file.getSize(), relatedType, relatedId, uploaderId);
        try {
            // 验证文件类型和大小
            validateFile(file);
            
            // 生成文件信息
            String originalName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalName);
            String fileName = generateFileName(fileExtension);
            String filePath = generateFilePath(relatedType, fileName);
            
            // 保存文件到磁盘
            saveFileToDisk(file, filePath);
            
            // 创建媒体文件记录
            MediaFile mediaFile = new MediaFile();
            mediaFile.setFileName(fileName);
            mediaFile.setOriginalName(originalName);
            mediaFile.setFilePath(filePath);
            mediaFile.setFileSize(file.getSize());
            mediaFile.setFileType(getFileType(fileExtension));
            mediaFile.setMimeType(file.getContentType());
            mediaFile.setRelatedType(relatedType);
            mediaFile.setRelatedId(relatedId);
            mediaFile.setUploaderId(uploaderId);
            mediaFile.setStatus(1); // 正常状态
            
            boolean result = save(mediaFile);
            if (result) {
                log.info("文件上传成功，ID: {}, 路径: {}", mediaFile.getId(), filePath);
                return mediaFile;
            } else {
                // 删除已保存的文件
                deleteFileFromDisk(filePath);
                log.error("文件上传失败");
                throw new BusinessException("文件上传失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("上传文件失败", e);
            throw new BusinessException("上传文件失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MediaFile> batchUploadFiles(List<MultipartFile> files, String relatedType, 
                                          Long relatedId, Long uploaderId) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException("文件列表不能为空");
        }
        
        log.info("批量上传文件，数量: {}", files.size());
        try {
            List<MediaFile> uploadedFiles = new ArrayList<>();
            
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    try {
                        MediaFile mediaFile = uploadFile(file, relatedType, relatedId, uploaderId);
                        uploadedFiles.add(mediaFile);
                    } catch (Exception e) {
                        log.error("批量上传中单个文件上传失败: {}", file.getOriginalFilename(), e);
                        // 继续处理其他文件
                    }
                }
            }
            
            log.info("批量上传完成，成功{}个，总共{}个", uploadedFiles.size(), files.size());
            return uploadedFiles;
        } catch (Exception e) {
            log.error("批量上传文件失败", e);
            throw new BusinessException("批量上传文件失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFile(MediaFile mediaFile) {
        if (mediaFile == null || mediaFile.getId() == null) {
            throw new BusinessException("媒体文件信息不完整");
        }
        
        log.info("更新媒体文件: ID={}", mediaFile.getId());
        try {
            // 验证文件是否存在
            MediaFile existingFile = getById(mediaFile.getId());
            if (existingFile == null) {
                throw new BusinessException("媒体文件不存在");
            }
            
            boolean result = updateById(mediaFile);
            if (result) {
                log.info("媒体文件更新成功");
            } else {
                log.error("媒体文件更新失败");
                throw new BusinessException("媒体文件更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新媒体文件失败", e);
            throw new BusinessException("更新媒体文件失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long fileId, Long userId) {
        if (fileId == null || userId == null) {
            throw new BusinessException("文件ID和用户ID不能为空");
        }

        log.info("删除媒体文件: ID={}, userId={}", fileId, userId);
        try {
            // 获取文件信息
            MediaFile mediaFile = getById(fileId);
            if (mediaFile == null) {
                throw new BusinessException("媒体文件不存在");
            }

            // 检查权限
            if (!hasPermission(userId, fileId)) {
                throw new BusinessException("无权限删除该文件");
            }

            // 删除数据库记录
            boolean result = removeById(fileId);
            if (result) {
                // 删除磁盘文件
                deleteFileFromDisk(mediaFile.getFilePath());
                log.info("媒体文件删除成功");
            } else {
                log.error("媒体文件删除失败");
                throw new BusinessException("媒体文件删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除媒体文件失败", e);
            throw new BusinessException("删除媒体文件失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteFiles(List<Long> fileIds, Long userId) {
        if (fileIds == null || fileIds.isEmpty() || userId == null) {
            throw new BusinessException("文件ID列表和用户ID不能为空");
        }

        log.info("批量删除媒体文件，数量: {}, userId={}", fileIds.size(), userId);
        try {
            int successCount = 0;

            for (Long fileId : fileIds) {
                try {
                    if (deleteFile(fileId, userId)) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("批量删除中单个文件删除失败，文件ID: {}", fileId, e);
                }
            }

            boolean result = successCount > 0;
            if (result) {
                log.info("批量删除完成，成功{}个，总共{}个", successCount, fileIds.size());
            } else {
                log.error("批量删除失败");
                throw new BusinessException("批量删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除媒体文件失败", e);
            throw new BusinessException("批量删除媒体文件失败");
        }
    }

    @Override
    public MediaFile getFileDetail(Long fileId) {
        if (fileId == null) {
            throw new BusinessException("文件ID不能为空");
        }
        
        log.debug("获取媒体文件详情: ID={}", fileId);
        try {
            MediaFile file = getById(fileId);
            if (file == null) {
                throw new BusinessException("媒体文件不存在");
            }
            return file;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取媒体文件详情失败", e);
            throw new BusinessException("获取媒体文件详情失败");
        }
    }

    @Override
    public boolean hasPermission(Long userId, Long fileId) {
        if (userId == null || fileId == null) {
            return false;
        }
        
        log.debug("检查用户媒体文件操作权限: userId={}, fileId={}", userId, fileId);
        try {
            MediaFile file = getById(fileId);
            if (file == null) {
                return false;
            }
            
            // 检查用户是否为文件上传者
            return file.getUploaderId().equals(userId);
        } catch (Exception e) {
            log.error("检查用户媒体文件操作权限失败", e);
            return false;
        }
    }

    @Override
    public String generateFileUrl(Long fileId) {
        if (fileId == null) {
            throw new BusinessException("文件ID不能为空");
        }
        
        log.debug("生成文件访问URL: fileId={}", fileId);
        try {
            MediaFile file = getById(fileId);
            if (file == null) {
                throw new BusinessException("媒体文件不存在");
            }
            
            // 获取系统配置的文件访问域名
            String baseUrl = systemConfigService.getConfigValue("file_base_url", "http://localhost:8080");
            return baseUrl + "/files/" + file.getFilePath();
        } catch (Exception e) {
            log.error("生成文件访问URL失败", e);
            throw new BusinessException("生成文件访问URL失败");
        }
    }

    @Override
    public String generateThumbnail(Long fileId, Integer width, Integer height) {
        if (fileId == null) {
            throw new BusinessException("文件ID不能为空");
        }
        
        log.debug("生成文件缩略图: fileId={}, width={}, height={}", fileId, width, height);
        try {
            MediaFile file = getById(fileId);
            if (file == null) {
                throw new BusinessException("媒体文件不存在");
            }
            
            // 只有图片类型才能生成缩略图
            if (!"image".equals(file.getFileType())) {
                throw new BusinessException("只有图片文件才能生成缩略图");
            }
            
            // TODO: 实现缩略图生成逻辑
            // 这里简化处理，返回原图URL
            return generateFileUrl(fileId);
        } catch (Exception e) {
            log.error("生成文件缩略图失败", e);
            throw new BusinessException("生成文件缩略图失败");
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        // 验证文件大小
        String maxSizeStr = systemConfigService.getConfigValue("max_file_size", "10485760"); // 默认10MB
        Long maxSize = Long.parseLong(maxSizeStr);
        if (!validateFileSize(file, maxSize)) {
            throw new BusinessException("文件大小超过限制");
        }
        
        // 验证文件类型
        List<String> allowedTypes = getSupportedFileTypes();
        if (!validateFileType(file, allowedTypes)) {
            throw new BusinessException("不支持的文件类型");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return timestamp + "_" + random + (StringUtils.hasText(extension) ? "." + extension : "");
    }

    /**
     * 生成文件路径
     */
    private String generateFilePath(String relatedType, String fileName) {
        String uploadPath = systemConfigService.getConfigValue("upload_path", "/uploads");
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return uploadPath + "/" + relatedType + "/" + datePath + "/" + fileName;
    }

    /**
     * 保存文件到磁盘
     */
    private void saveFileToDisk(MultipartFile file, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        file.transferTo(path.toFile());
    }

    /**
     * 从磁盘删除文件
     */
    private void deleteFileFromDisk(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("删除磁盘文件失败: {}", filePath, e);
        }
    }

    @Override
    public int countFilesByUploader(Long uploaderId, String fileType) {
        if (uploaderId == null) {
            return 0;
        }

        return mediaFileMapper.countByUploader(uploaderId, fileType);
    }

    @Override
    public int countFilesByRelated(String relatedType, Long relatedId) {
        if (!StringUtils.hasText(relatedType) || relatedId == null) {
            return 0;
        }

        return mediaFileMapper.countByRelated(relatedType, relatedId);
    }

    @Override
    public Object getStorageStatistics(Long uploaderId) {
        log.debug("获取文件存储统计信息，上传者ID: {}", uploaderId);
        try {
            Map<String, Object> statistics = new HashMap<>();

            if (uploaderId != null) {
                // 特定用户的统计
                statistics.put("totalFiles", countFilesByUploader(uploaderId, null));
                statistics.put("imageFiles", countFilesByUploader(uploaderId, "image"));
                statistics.put("videoFiles", countFilesByUploader(uploaderId, "video"));
                statistics.put("audioFiles", countFilesByUploader(uploaderId, "audio"));
                statistics.put("documentFiles", countFilesByUploader(uploaderId, "document"));
                statistics.put("otherFiles", countFilesByUploader(uploaderId, "other"));

                // 计算总存储大小
                statistics.put("totalSize", calculateTotalSize(uploaderId));
            } else {
                // 全局统计
                statistics.put("totalFiles", count());
                statistics.put("imageFiles", count(new LambdaQueryWrapper<MediaFile>().eq(MediaFile::getFileType, "image")));
                statistics.put("videoFiles", count(new LambdaQueryWrapper<MediaFile>().eq(MediaFile::getFileType, "video")));
                statistics.put("audioFiles", count(new LambdaQueryWrapper<MediaFile>().eq(MediaFile::getFileType, "audio")));
                statistics.put("documentFiles", count(new LambdaQueryWrapper<MediaFile>().eq(MediaFile::getFileType, "document")));
                statistics.put("otherFiles", count(new LambdaQueryWrapper<MediaFile>().eq(MediaFile::getFileType, "other")));

                // 计算总存储大小
                statistics.put("totalSize", calculateTotalSize(null));
            }

            return statistics;
        } catch (Exception e) {
            log.error("获取文件存储统计信息失败", e);
            throw new BusinessException("获取存储统计信息失败");
        }
    }

    @Override
    public int cleanupInvalidFiles() {
        log.info("开始清理无效文件");
        try {
            int cleanedCount = 0;

            // 查询所有文件记录
            List<MediaFile> allFiles = list();

            for (MediaFile file : allFiles) {
                // 检查文件是否存在
                if (!fileExists(file.getFilePath())) {
                    // 删除数据库记录
                    if (removeById(file.getId())) {
                        cleanedCount++;
                        log.debug("清理无效文件记录: {}", file.getFilePath());
                    }
                }
            }

            log.info("清理无效文件完成，清理{}个文件", cleanedCount);
            return cleanedCount;
        } catch (Exception e) {
            log.error("清理无效文件失败", e);
            throw new BusinessException("清理无效文件失败");
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return false;
        }

        try {
            Path path = Paths.get(filePath);
            return Files.exists(path);
        } catch (Exception e) {
            log.error("检查文件是否存在失败: {}", filePath, e);
            return false;
        }
    }

    @Override
    public List<String> getSupportedFileTypes() {
        log.debug("获取支持的文件类型列表");
        // 返回支持的文件扩展名列表
        return Arrays.asList(
            // 图片
            "jpg", "jpeg", "png", "gif", "bmp", "webp",
            // 视频
            "mp4", "avi", "mov", "wmv", "flv", "mkv",
            // 音频
            "mp3", "wav", "flac", "aac", "ogg",
            // 文档
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"
        );
    }

    @Override
    public boolean validateFileType(MultipartFile file, List<String> allowedTypes) {
        if (file == null || allowedTypes == null || allowedTypes.isEmpty()) {
            return false;
        }

        String originalName = file.getOriginalFilename();
        if (!StringUtils.hasText(originalName)) {
            return false;
        }

        String extension = getFileExtension(originalName);
        return allowedTypes.contains(extension.toLowerCase());
    }

    @Override
    public boolean validateFileSize(MultipartFile file, Long maxSize) {
        if (file == null || maxSize == null || maxSize <= 0) {
            return false;
        }

        return file.getSize() <= maxSize;
    }

    @Override
    public boolean updateFileStatus(Long fileId, Integer status) {
        if (fileId == null || status == null) {
            throw new BusinessException("文件ID和状态不能为空");
        }

        log.info("更新文件状态: fileId={}, status={}", fileId, status);
        try {
            MediaFile mediaFile = new MediaFile();
            mediaFile.setId(fileId);
            mediaFile.setStatus(status);

            boolean result = updateById(mediaFile);
            if (result) {
                log.info("文件状态更新成功");
            } else {
                log.error("文件状态更新失败");
                throw new BusinessException("文件状态更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新文件状态失败", e);
            throw new BusinessException("更新文件状态失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateFileStatus(List<Long> fileIds, Integer status) {
        if (fileIds == null || fileIds.isEmpty() || status == null) {
            throw new BusinessException("文件ID列表和状态不能为空");
        }

        log.info("批量更新文件状态，数量: {}, status={}", fileIds.size(), status);
        try {
            int successCount = 0;

            for (Long fileId : fileIds) {
                try {
                    if (updateFileStatus(fileId, status)) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("批量更新中单个文件状态更新失败，文件ID: {}", fileId, e);
                }
            }

            boolean result = successCount > 0;
            if (result) {
                log.info("批量更新文件状态完成，成功{}个，总共{}个", successCount, fileIds.size());
            } else {
                log.error("批量更新文件状态失败");
                throw new BusinessException("批量更新文件状态失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量更新文件状态失败", e);
            throw new BusinessException("批量更新文件状态失败");
        }
    }

    @Override
    public Object getUploadConfig() {
        log.debug("获取文件上传配置");
        try {
            Map<String, Object> config = new HashMap<>();

            // 最大文件大小
            String maxSizeStr = systemConfigService.getConfigValue("max_file_size", "10485760");
            config.put("maxFileSize", Long.parseLong(maxSizeStr));

            // 支持的文件类型
            config.put("supportedTypes", getSupportedFileTypes());

            // 上传路径
            config.put("uploadPath", systemConfigService.getConfigValue("upload_path", "/uploads"));

            // 文件访问域名
            config.put("baseUrl", systemConfigService.getConfigValue("file_base_url", "http://localhost:8080"));

            return config;
        } catch (Exception e) {
            log.error("获取文件上传配置失败", e);
            throw new BusinessException("获取上传配置失败");
        }
    }

    /**
     * 根据扩展名获取文件类型
     */
    private String getFileType(String extension) {
        if (!StringUtils.hasText(extension)) {
            return "other";
        }

        extension = extension.toLowerCase();
        if (Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp").contains(extension)) {
            return "image";
        } else if (Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "mkv").contains(extension)) {
            return "video";
        } else if (Arrays.asList("mp3", "wav", "flac", "aac", "ogg").contains(extension)) {
            return "audio";
        } else if (Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt").contains(extension)) {
            return "document";
        } else {
            return "other";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean compressImage(Long fileId, Integer quality) {
        if (fileId == null || quality == null || quality < 0 || quality > 100) {
            throw new BusinessException("参数无效");
        }

        log.info("压缩图片文件: fileId={}, quality={}", fileId, quality);
        try {
            MediaFile mediaFile = getById(fileId);
            if (mediaFile == null) {
                throw new BusinessException("媒体文件不存在");
            }

            // 只有图片类型才能压缩
            if (!"image".equals(mediaFile.getFileType())) {
                throw new BusinessException("只有图片文件才能压缩");
            }

            // TODO: 实现图片压缩逻辑
            // 这里简化处理，返回成功
            log.info("图片压缩完成（模拟）");
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("压缩图片文件失败", e);
            throw new BusinessException("压缩图片文件失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaFile convertFileFormat(Long fileId, String targetFormat) {
        if (fileId == null || !StringUtils.hasText(targetFormat)) {
            throw new BusinessException("参数不能为空");
        }

        log.info("转换文件格式: fileId={}, targetFormat={}", fileId, targetFormat);
        try {
            MediaFile originalFile = getById(fileId);
            if (originalFile == null) {
                throw new BusinessException("媒体文件不存在");
            }

            // 检查目标格式是否支持
            List<String> supportedTypes = getSupportedFileTypes();
            if (!supportedTypes.contains(targetFormat.toLowerCase())) {
                throw new BusinessException("不支持的目标格式");
            }

            // TODO: 实现文件格式转换逻辑
            // 这里简化处理，创建一个新的文件记录
            MediaFile convertedFile = new MediaFile();
            convertedFile.setFileName(generateFileName(targetFormat));
            convertedFile.setOriginalName(originalFile.getOriginalName());
            convertedFile.setFilePath(generateFilePath(originalFile.getRelatedType(), convertedFile.getFileName()));
            convertedFile.setFileSize(originalFile.getFileSize());
            convertedFile.setFileType(getFileType(targetFormat));
            convertedFile.setMimeType("application/" + targetFormat);
            convertedFile.setRelatedType(originalFile.getRelatedType());
            convertedFile.setRelatedId(originalFile.getRelatedId());
            convertedFile.setUploaderId(originalFile.getUploaderId());
            convertedFile.setStatus(1);

            boolean result = save(convertedFile);
            if (result) {
                log.info("文件格式转换完成（模拟），新文件ID: {}", convertedFile.getId());
                return convertedFile;
            } else {
                log.error("文件格式转换失败");
                throw new BusinessException("文件格式转换失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("转换文件格式失败", e);
            throw new BusinessException("转换文件格式失败");
        }
    }

    /**
     * 计算总存储大小
     */
    private Long calculateTotalSize(Long uploaderId) {
        try {
            LambdaQueryWrapper<MediaFile> wrapper = new LambdaQueryWrapper<>();
            if (uploaderId != null) {
                wrapper.eq(MediaFile::getUploaderId, uploaderId);
            }
            wrapper.select(MediaFile::getFileSize);

            List<MediaFile> files = list(wrapper);
            return files.stream()
                    .map(MediaFile::getFileSize)
                    .filter(Objects::nonNull)
                    .reduce(0L, Long::sum);
        } catch (Exception e) {
            log.error("计算总存储大小失败", e);
            return 0L;
        }
    }
}
