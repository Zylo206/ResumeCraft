package com.zylo.resumecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "resume_module", autoResultMap = true)
public class ResumeModule {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long resumeId;

    /** 模块类型: basic_info/education/internship/work_experience/project/skill/paper/research/award/job_intention */
    private String moduleType;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> content;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

