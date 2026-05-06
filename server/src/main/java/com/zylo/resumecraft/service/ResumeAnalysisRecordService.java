package com.zylo.resumecraft.service;

import com.zylo.resumecraft.dto.ResumeAnalysisResultDTO;
import com.zylo.resumecraft.entity.ResumeAnalysisRecord;

public interface ResumeAnalysisRecordService {
    void save(ResumeAnalysisRecord record);

    ResumeAnalysisResultDTO getLatestCompletedRecord(Long userId, Long resumeId);
}

