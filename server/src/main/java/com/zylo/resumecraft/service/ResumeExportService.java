package com.zylo.resumecraft.service;

import com.zylo.resumecraft.dto.ResumeExportRequestDTO;

public interface ResumeExportService {
    ExportedResumeFile exportPdf(Long resumeId, Long userId, ResumeExportRequestDTO request);

    record ExportedResumeFile(byte[] content, String fileName) {
    }
}

