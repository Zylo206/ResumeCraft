CREATE DATABASE IF NOT EXISTS `pai_resume`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `pai_resume`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `email`      VARCHAR(128) NOT NULL COMMENT '邮箱（登录账号）',
    `password`   VARCHAR(255) NOT NULL COMMENT 'BCrypt 加密密码',
    `nickname`   VARCHAR(64)  DEFAULT '' COMMENT '昵称',
    `avatar`     VARCHAR(512) DEFAULT '' COMMENT '头像 URL',
    `role`       TINYINT      NOT NULL DEFAULT 0 COMMENT '角色: 0=普通用户, 1=管理员',
    `status`     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0=禁用, 1=正常',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 简历表
CREATE TABLE IF NOT EXISTS `resume` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT       NOT NULL COMMENT '所属用户 ID',
    `title`       VARCHAR(128) NOT NULL DEFAULT '未命名简历' COMMENT '简历标题',
    `template_id` VARCHAR(64)  DEFAULT 'default' COMMENT '模板标识',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0=已删除, 1=正常',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_updated_at` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历表';

-- 简历模块表
CREATE TABLE IF NOT EXISTS `resume_module` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `resume_id`   BIGINT       NOT NULL COMMENT '简历 ID',
    `module_type` VARCHAR(32)  NOT NULL COMMENT '模块类型: basic_info/education/internship/work_experience/project/skill/paper/research/award/job_intention',
    `content`     JSON         NOT NULL COMMENT '模块内容 JSON',
    `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '排序序号',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_resume_type` (`resume_id`, `module_type`),
    KEY `idx_resume_type_sort` (`resume_id`, `module_type`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历模块表';
