CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(128) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `nickname` VARCHAR(64) DEFAULT '',
    `avatar` VARCHAR(512) DEFAULT '',
    `role` TINYINT NOT NULL DEFAULT 0,
    `status` TINYINT NOT NULL DEFAULT 1,
    `membership_status` VARCHAR(16) NOT NULL DEFAULT 'FREE',
    `membership_granted_at` DATETIME NULL,
    `membership_source` VARCHAR(32) NULL,
    `membership_expires_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_email` UNIQUE (`email`)
);

CREATE TABLE IF NOT EXISTS `user_auth_identity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `provider` VARCHAR(32) NOT NULL,
    `principal` VARCHAR(191) NOT NULL,
    `credential_hash` VARCHAR(255) NULL,
    `verified_at` DATETIME NULL,
    `status` TINYINT NOT NULL DEFAULT 1,
    `last_login_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_provider_principal` UNIQUE (`provider`, `principal`)
);

CREATE INDEX IF NOT EXISTS `idx_user_provider` ON `user_auth_identity` (`user_id`, `provider`);

CREATE TABLE IF NOT EXISTS `resume` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(128) NOT NULL DEFAULT '未命名简历',
    `template_id` VARCHAR(64) DEFAULT 'default',
    `status` TINYINT NOT NULL DEFAULT 1,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE INDEX IF NOT EXISTS `idx_resume_user_id` ON `resume` (`user_id`);
CREATE INDEX IF NOT EXISTS `idx_resume_updated_at` ON `resume` (`updated_at`);

CREATE TABLE IF NOT EXISTS `resume_module` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `resume_id` BIGINT NOT NULL,
    `module_type` VARCHAR(32) NOT NULL,
    `content` LONGTEXT NOT NULL,
    `sort_order` INT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE INDEX IF NOT EXISTS `idx_resume_type` ON `resume_module` (`resume_id`, `module_type`);
CREATE INDEX IF NOT EXISTS `idx_resume_type_sort` ON `resume_module` (`resume_id`, `module_type`, `sort_order`);

CREATE TABLE IF NOT EXISTS `platform_config` (
    `id` BIGINT NOT NULL DEFAULT 1,
    `membership_price_cents` INT NOT NULL DEFAULT 6600,
    `questionnaire_coupon_amount_cents` INT NOT NULL DEFAULT 1000,
    `updated_by` BIGINT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

MERGE INTO `platform_config` (`id`, `membership_price_cents`, `questionnaire_coupon_amount_cents`)
KEY (`id`) VALUES (1, 6600, 1000);

CREATE TABLE IF NOT EXISTS `feedback_submission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `contact_email` VARCHAR(128) NOT NULL,
    `display_name` VARCHAR(64) NOT NULL DEFAULT '',
    `school_or_company` VARCHAR(128) NOT NULL DEFAULT '',
    `target_role` VARCHAR(128) NOT NULL DEFAULT '',
    `rating` TINYINT NOT NULL,
    `testimonial_text` TEXT NOT NULL,
    `desired_features` LONGTEXT NULL,
    `bug_feedback` LONGTEXT NULL,
    `consent_to_publish` TINYINT NOT NULL DEFAULT 0,
    `review_status` VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    `publish_status` VARCHAR(16) NOT NULL DEFAULT 'UNPUBLISHED',
    `coupon_status` VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    `review_note` VARCHAR(512) NULL,
    `reviewed_by` BIGINT NULL,
    `reviewed_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE INDEX IF NOT EXISTS `idx_feedback_review_status` ON `feedback_submission` (`review_status`, `created_at`);
CREATE INDEX IF NOT EXISTS `idx_feedback_publish_status` ON `feedback_submission` (`publish_status`, `created_at`);
CREATE INDEX IF NOT EXISTS `idx_feedback_contact_email` ON `feedback_submission` (`contact_email`);

CREATE TABLE IF NOT EXISTS `coupon_code` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(64) NOT NULL,
    `source_type` VARCHAR(32) NOT NULL,
    `source_id` BIGINT NULL,
    `recipient_email` VARCHAR(128) NOT NULL,
    `amount_cents` INT NOT NULL,
    `status` VARCHAR(16) NOT NULL DEFAULT 'ISSUED',
    `used_by_user_id` BIGINT NULL,
    `used_at` DATETIME NULL,
    `email_sent_at` DATETIME NULL,
    `expires_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_coupon_code` UNIQUE (`code`),
    CONSTRAINT `uk_coupon_source` UNIQUE (`source_type`, `source_id`)
);

CREATE INDEX IF NOT EXISTS `idx_coupon_status` ON `coupon_code` (`status`, `created_at`);
CREATE INDEX IF NOT EXISTS `idx_coupon_email` ON `coupon_code` (`recipient_email`);

CREATE TABLE IF NOT EXISTS `resume_showcase` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `resume_id` BIGINT NOT NULL,
    `slug` VARCHAR(128) NOT NULL,
    `score_label` VARCHAR(64) NOT NULL DEFAULT '',
    `summary` VARCHAR(512) NOT NULL DEFAULT '',
    `tags` LONGTEXT NULL,
    `display_order` INT NOT NULL DEFAULT 0,
    `publish_status` VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_resume_showcase_slug` UNIQUE (`slug`),
    CONSTRAINT `uk_resume_showcase_resume` UNIQUE (`resume_id`)
);

CREATE INDEX IF NOT EXISTS `idx_resume_showcase_publish_order`
ON `resume_showcase` (`publish_status`, `display_order`, `updated_at`);

CREATE TABLE IF NOT EXISTS `ai_optimize_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `resume_id` BIGINT NOT NULL,
    `module_id` BIGINT NOT NULL,
    `module_type` VARCHAR(32) NOT NULL,
    `field_type` VARCHAR(32) NOT NULL,
    `field_index` INT NULL,
    `record_status` VARCHAR(16) NOT NULL,
    `original_text` TEXT NULL,
    `reasoning_markdown` LONGTEXT NULL,
    `streamed_content` LONGTEXT NULL,
    `optimized_text` TEXT NULL,
    `candidates` LONGTEXT NULL,
    `prompt` TEXT NULL,
    `system_prompt` TEXT NULL,
    `error_message` VARCHAR(512) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE INDEX IF NOT EXISTS `idx_ai_user_created_at` ON `ai_optimize_record` (`user_id`, `created_at`);
CREATE INDEX IF NOT EXISTS `idx_resume_module_field_created`
ON `ai_optimize_record` (`resume_id`, `module_id`, `field_type`, `field_index`, `created_at`);

CREATE TABLE IF NOT EXISTS `resume_analysis_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `resume_id` BIGINT NOT NULL,
    `record_status` VARCHAR(16) NOT NULL,
    `score` INT NULL,
    `issues` LONGTEXT NULL,
    `suggestions` LONGTEXT NULL,
    `prompt` TEXT NULL,
    `error_message` VARCHAR(512) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE INDEX IF NOT EXISTS `idx_analysis_user_created_at` ON `resume_analysis_record` (`user_id`, `created_at`);
CREATE INDEX IF NOT EXISTS `idx_resume_status_created`
ON `resume_analysis_record` (`resume_id`, `record_status`, `created_at`);
