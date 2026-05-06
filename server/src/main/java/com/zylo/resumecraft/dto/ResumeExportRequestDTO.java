package com.zylo.resumecraft.dto;

import lombok.Data;

@Data
public class ResumeExportRequestDTO {
    private String pageMode;
    private String templateId;
    private String density;
    private String accentPreset;
    private String headingStyle;
}

