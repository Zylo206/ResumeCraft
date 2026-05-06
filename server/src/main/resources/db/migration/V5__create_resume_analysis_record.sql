CREATE TABLE IF NOT EXISTS `resume_analysis_record` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`       BIGINT       NOT NULL COMMENT '用户 ID',
    `resume_id`     BIGINT       NOT NULL COMMENT '简历 ID',
    `record_status` VARCHAR(16)  NOT NULL COMMENT '状态: completed/error',
    `score`         INT          NULL COMMENT '简历评分',
    `issues`        JSON         NULL COMMENT '问题列表',
    `suggestions`   JSON         NULL COMMENT '建议列表',
    `prompt`        TEXT         NULL COMMENT '本次请求提示词',
    `error_message` VARCHAR(512) NULL COMMENT '失败原因',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_created_at` (`user_id`, `created_at`),
    KEY `idx_resume_status_created` (`resume_id`, `record_status`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='整份简历 AI 分析记录表';
