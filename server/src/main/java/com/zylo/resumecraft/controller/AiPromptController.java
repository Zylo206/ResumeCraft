package com.zylo.resumecraft.controller;

import com.zylo.resumecraft.common.Result;
import com.zylo.resumecraft.dto.FieldOptimizePromptConfigDTO;
import com.zylo.resumecraft.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI 提示词配置接口")
@RestController
@RequestMapping("/resumes/field-optimize-prompts")
public class AiPromptController {

    private final AiService aiService;

    public AiPromptController(AiService aiService) {
        this.aiService = aiService;
    }

    @Operation(summary = "获取字段优化默认提示词配置")
    @GetMapping
    public Result<FieldOptimizePromptConfigDTO> getFieldOptimizePromptConfig() {
        return Result.success(aiService.getFieldOptimizePromptConfig());
    }
}

