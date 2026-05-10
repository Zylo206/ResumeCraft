package com.zylo.resumecraft.service;

import com.zylo.resumecraft.dto.ResumeAnalysisResultDTO;
import com.zylo.resumecraft.dto.AiFieldOptimizeRequestDTO;
import com.zylo.resumecraft.dto.FieldOptimizePromptConfigDTO;
import com.zylo.resumecraft.dto.SmartOnePagePreviewRequestDTO;
import com.zylo.resumecraft.dto.SmartOnePagePreviewResponseDTO;
import com.zylo.resumecraft.entity.ResumeModule;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface AiService {
    Map<String, Object> optimizeModule(String moduleType, Map<String, Object> content);

    Map<String, Object> optimizeModule(String moduleType, Map<String, Object> content, String language);

    Map<String, Object> optimizeModuleField(String moduleType, Map<String, Object> content, AiFieldOptimizeRequestDTO request);

    Map<String, Object> optimizeModuleField(String moduleType, Map<String, Object> content, AiFieldOptimizeRequestDTO request, String language);

    Map<String, Object> streamOptimizeModuleField(
            String moduleType,
            Map<String, Object> content,
            AiFieldOptimizeRequestDTO request,
            Consumer<Map<String, Object>> eventConsumer
    );

    Map<String, Object> streamOptimizeModuleField(
            String moduleType,
            Map<String, Object> content,
            AiFieldOptimizeRequestDTO request,
            Consumer<Map<String, Object>> eventConsumer,
            String language
    );

    FieldOptimizePromptConfigDTO getFieldOptimizePromptConfig();

    ResumeAnalysisResultDTO analyzeResume(String resumeTitle, List<ResumeModule> modules, String promptOverride);

    ResumeAnalysisResultDTO analyzeResume(String resumeTitle, List<ResumeModule> modules, String promptOverride, String language);

    ResumeAnalysisResultDTO streamAnalyzeResume(
            String resumeTitle,
            List<ResumeModule> modules,
            String promptOverride,
            Consumer<Map<String, Object>> eventConsumer
    );

    ResumeAnalysisResultDTO streamAnalyzeResume(
            String resumeTitle,
            List<ResumeModule> modules,
            String promptOverride,
            Consumer<Map<String, Object>> eventConsumer,
            String language
    );

    SmartOnePagePreviewResponseDTO previewSmartOnePage(
            String resumeTitle,
            List<ResumeModule> modules,
            SmartOnePagePreviewRequestDTO request
    );

    SmartOnePagePreviewResponseDTO previewSmartOnePage(
            String resumeTitle,
            List<ResumeModule> modules,
            SmartOnePagePreviewRequestDTO request,
            String language
    );
}
