package com.ifarm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 媒体文件实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("media_files")
@Schema(name = "MediaFile", description = "媒体文件信息")
public class MediaFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "文件ID", example = "1")
    private Long id;

    /**
     * 文件名
     */
    @TableField("file_name")
    @Schema(description = "文件名", example = "20240601_growth_record.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fileName;

    /**
     * 原始文件名
     */
    @TableField("original_name")
    @Schema(description = "原始文件名", example = "生长记录照片.jpg")
    private String originalName;

    /**
     * 文件路径
     */
    @TableField("file_path")
    @Schema(description = "文件路径", example = "/uploads/images/2024/06/01/20240601_growth_record.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String filePath;

    /**
     * 访问URL
     */
    @TableField("file_url")
    @Schema(description = "访问URL", example = "https://cdn.ifarm.com/images/2024/06/01/20240601_growth_record.jpg")
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    @Schema(description = "文件大小", example = "1024000")
    private Long fileSize;

    /**
     * 文件类型：image,video,document
     */
    @TableField("file_type")
    @Schema(description = "文件类型", example = "image", allowableValues = {"image", "video", "document"})
    private String fileType;

    /**
     * MIME类型
     */
    @TableField("mime_type")
    @Schema(description = "MIME类型", example = "image/jpeg")
    private String mimeType;

    /**
     * 文件扩展名
     */
    @TableField("file_extension")
    @Schema(description = "文件扩展名", example = "jpg")
    private String fileExtension;

    /**
     * 图片/视频宽度
     */
    @TableField("width")
    @Schema(description = "图片/视频宽度", example = "1920")
    private Integer width;

    /**
     * 图片/视频高度
     */
    @TableField("height")
    @Schema(description = "图片/视频高度", example = "1080")
    private Integer height;

    /**
     * 视频时长（秒）
     */
    @TableField("duration")
    @Schema(description = "视频时长", example = "120")
    private Integer duration;

    /**
     * 关联类型：user,farm,project,growth,harvest
     */
    @TableField("related_type")
    @Schema(description = "关联类型", example = "growth", allowableValues = {"user", "farm", "project", "growth", "harvest"})
    private String relatedType;

    /**
     * 关联ID
     */
    @TableField("related_id")
    @Schema(description = "关联ID", example = "1")
    private Long relatedId;

    /**
     * 上传者ID
     */
    @TableField("uploader_id")
    @Schema(description = "上传者ID", example = "1")
    private Long uploaderId;

    /**
     * 状态：0-禁用，1-正常
     */
    @TableField("status")
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    @Schema(description = "是否删除", example = "0", allowableValues = {"0", "1"})
    private Integer deleted;
}
