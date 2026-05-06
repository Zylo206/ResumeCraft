package com.zylo.resumecraft.dto;

import lombok.Data;

@Data
public class SmartOnePagePreviewRequestDTO {
    private String mode;
    private String promptMode;
    private String skillId;
    private String customPrompt;
    private String templateId;
    private String adoptionPolicy;
    private String outputFormat;
}

