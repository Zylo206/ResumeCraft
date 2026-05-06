package com.zylo.resumecraft.dto;

import com.zylo.resumecraft.entity.ResumeModule;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SmartOnePagePreviewResponseDTO {
    private List<ResumeModule> originalModules = new ArrayList<>();
    private List<ResumeModule> optimizedModules = new ArrayList<>();
    private List<SmartOnePageModuleDecisionDTO> moduleDecisions = new ArrayList<>();
    private List<ResumeModule> effectiveModules = new ArrayList<>();
    private SmartOnePagePreviewMetaDTO previewMeta;
    private String summary;
}

