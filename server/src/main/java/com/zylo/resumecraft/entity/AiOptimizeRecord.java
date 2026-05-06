package com.zylo.resumecraft.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "ai_optimize_record", autoResultMap = true)
public class AiOptimizeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long resumeId;

    private Long moduleId;

    private String moduleType;

    private String fieldType;

    private Integer fieldIndex;

    private String recordStatus;

    private String originalText;

    private String reasoningMarkdown;

    private String streamedContent;

    private String optimizedText;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> candidates;

    private String prompt;

    private String systemPrompt;

    private String errorMessage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

