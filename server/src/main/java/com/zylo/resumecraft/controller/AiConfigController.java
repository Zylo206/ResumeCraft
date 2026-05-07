package com.zylo.resumecraft.controller;

import com.zylo.resumecraft.common.Result;
import com.zylo.resumecraft.service.AiConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.local.enabled", havingValue = "true")
public class AiConfigController {

    private final AiConfigService aiConfigService;

    @GetMapping("/ai-config")
    public Result<Map<String, String>> getConfig() {
        return Result.success(aiConfigService.getConfig());
    }

    @PutMapping("/ai-config")
    public Result<Map<String, String>> updateConfig(@RequestBody Map<String, String> body) {
        aiConfigService.updateConfig(body);
        return Result.success(aiConfigService.getConfig());
    }
}
