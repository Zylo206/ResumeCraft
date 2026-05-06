package com.zylo.resumecraft.dto;

import lombok.Data;

@Data
public class AiFieldOptimizeRequestDTO {
    private String fieldType;
    private Integer index;
    private String prompt;
    private String systemPrompt;
}

