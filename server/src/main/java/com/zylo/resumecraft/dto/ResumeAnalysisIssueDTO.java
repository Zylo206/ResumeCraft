package com.zylo.resumecraft.dto;

import lombok.Data;

@Data
public class ResumeAnalysisIssueDTO {
    private String type;
    private String field;
    private String message;
    private String suggestion;
}

