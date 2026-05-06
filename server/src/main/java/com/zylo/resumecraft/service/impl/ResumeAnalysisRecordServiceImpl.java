package com.zylo.resumecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zylo.resumecraft.dto.ResumeAnalysisResultDTO;
import com.zylo.resumecraft.entity.ResumeAnalysisRecord;
import com.zylo.resumecraft.mapper.ResumeAnalysisRecordMapper;
import com.zylo.resumecraft.service.ResumeAnalysisRecordService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ResumeAnalysisRecordServiceImpl implements ResumeAnalysisRecordService {

    private final ResumeAnalysisRecordMapper resumeAnalysisRecordMapper;

    public ResumeAnalysisRecordServiceImpl(ResumeAnalysisRecordMapper resumeAnalysisRecordMapper) {
        this.resumeAnalysisRecordMapper = resumeAnalysisRecordMapper;
    }

    @Override
    public void save(ResumeAnalysisRecord record) {
        if (record == null) {
            return;
        }
        resumeAnalysisRecordMapper.insert(record);
    }

    @Override
    public ResumeAnalysisResultDTO getLatestCompletedRecord(Long userId, Long resumeId) {
        var record = resumeAnalysisRecordMapper.selectOne(
                new LambdaQueryWrapper<ResumeAnalysisRecord>()
                        .eq(ResumeAnalysisRecord::getUserId, userId)
                        .eq(ResumeAnalysisRecord::getResumeId, resumeId)
                        .eq(ResumeAnalysisRecord::getRecordStatus, "completed")
                        .orderByDesc(ResumeAnalysisRecord::getCreatedAt)
                        .orderByDesc(ResumeAnalysisRecord::getId)
                        .last("LIMIT 1")
        );
        if (record == null) {
            return null;
        }

        var dto = new ResumeAnalysisResultDTO();
        dto.setScore(record.getScore() == null ? 0 : record.getScore());
        dto.setIssues(record.getIssues() == null ? new ArrayList<>() : record.getIssues());
        dto.setSuggestions(record.getSuggestions() == null ? new ArrayList<>() : record.getSuggestions());
        return dto;
    }
}

