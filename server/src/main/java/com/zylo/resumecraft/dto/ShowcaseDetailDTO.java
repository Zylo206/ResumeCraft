package com.zylo.resumecraft.dto;

import com.zylo.resumecraft.entity.ResumeModule;
import lombok.Data;

import java.util.List;

@Data
public class ShowcaseDetailDTO {
    private Long id;
    private String slug;
    private String title;
    private String scoreLabel;
    private String summary;
    private List<String> tags;
    private List<ResumeModule> modules;
    private String updatedAt;
}

