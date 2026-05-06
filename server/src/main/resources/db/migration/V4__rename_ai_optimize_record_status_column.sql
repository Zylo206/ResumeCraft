ALTER TABLE `ai_optimize_record`
    CHANGE COLUMN `status` `record_status` VARCHAR(16) NOT NULL COMMENT '状态: completed/error';
