package com.zylo.resumecraft.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResumeCreateDTO {
    private String title;
    private String templateId;
    private String language;
}

