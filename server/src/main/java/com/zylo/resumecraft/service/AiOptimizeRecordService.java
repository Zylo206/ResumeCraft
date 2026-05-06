package com.zylo.resumecraft.service;

import com.zylo.resumecraft.dto.AiFieldOptimizeRecordDTO;
import com.zylo.resumecraft.entity.AiOptimizeRecord;

public interface AiOptimizeRecordService {
    void save(AiOptimizeRecord record);

    AiFieldOptimizeRecordDTO getLatestRecord(Long userId, Long resumeId, Long moduleId, String fieldType, Integer fieldIndex);
}

