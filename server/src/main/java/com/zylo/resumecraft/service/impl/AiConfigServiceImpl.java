package com.zylo.resumecraft.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zylo.resumecraft.service.AiConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.local.enabled", havingValue = "true")
public class AiConfigServiceImpl implements AiConfigService {

    private final ObjectMapper objectMapper;
    private final Path configPath;

    @Value("${ai.api-key:}")
    private String defaultApiKey;

    @Value("${ai.base-url:https://api.deepseek.com/v1}")
    private String defaultBaseUrl;

    @Value("${ai.model:deepseek-chat}")
    private String defaultModel;

    @Value("${ai.analysis-model:${ai.model:deepseek-chat}}")
    private String defaultAnalysisModel;

    public AiConfigServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        String dataDir = System.getProperty("resumecraft.data-dir", System.getProperty("user.dir"));
        this.configPath = Path.of(dataDir, "ai-config.json");
    }

    @Override
    public Map<String, String> getConfig() {
        Map<String, String> result = new LinkedHashMap<>();
        Map<String, String> stored = readStoredConfig();

        result.put("apiKey", maskApiKey(stored.getOrDefault("apiKey", defaultApiKey)));
        result.put("baseUrl", stored.getOrDefault("baseUrl", defaultBaseUrl));
        result.put("model", stored.getOrDefault("model", defaultModel));
        result.put("analysisModel", stored.getOrDefault("analysisModel", defaultAnalysisModel));

        return result;
    }

    @Override
    public void updateConfig(Map<String, String> updates) {
        Map<String, String> stored = readStoredConfig();

        if (updates.containsKey("apiKey")) {
            String val = updates.get("apiKey");
            if (val != null && !val.isBlank()) {
                stored.put("apiKey", val.trim());
            }
        }
        if (updates.containsKey("baseUrl")) {
            String val = updates.get("baseUrl");
            if (val != null && !val.isBlank()) {
                stored.put("baseUrl", val.trim());
            }
        }
        if (updates.containsKey("model")) {
            String val = updates.get("model");
            if (val != null && !val.isBlank()) {
                stored.put("model", val.trim());
            }
        }
        if (updates.containsKey("analysisModel")) {
            String val = updates.get("analysisModel");
            if (val != null && !val.isBlank()) {
                stored.put("analysisModel", val.trim());
            }
        }

        writeStoredConfig(stored);
        log.info("AI config updated");
    }

    @Override
    public String getApiKey() {
        String stored = readStoredConfig().get("apiKey");
        return (stored != null && !stored.isBlank()) ? stored : defaultApiKey;
    }

    @Override
    public String getBaseUrl() {
        String stored = readStoredConfig().get("baseUrl");
        return (stored != null && !stored.isBlank()) ? stored : defaultBaseUrl;
    }

    @Override
    public String getModel() {
        String stored = readStoredConfig().get("model");
        return (stored != null && !stored.isBlank()) ? stored : defaultModel;
    }

    @Override
    public String getAnalysisModel() {
        String stored = readStoredConfig().get("analysisModel");
        return (stored != null && !stored.isBlank()) ? stored : defaultAnalysisModel;
    }

    private Map<String, String> readStoredConfig() {
        if (!Files.exists(configPath)) {
            return new HashMap<>();
        }
        try {
            byte[] bytes = Files.readAllBytes(configPath);
            if (bytes.length == 0) {
                return new HashMap<>();
            }
            return objectMapper.readValue(bytes, new TypeReference<>() {});
        } catch (IOException e) {
            log.warn("Failed to read AI config file: {}", configPath, e);
            return new HashMap<>();
        }
    }

    private void writeStoredConfig(Map<String, String> config) {
        try {
            Files.createDirectories(configPath.getParent());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(configPath.toFile(), config);
        } catch (IOException e) {
            log.error("Failed to write AI config file: {}", configPath, e);
        }
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return "";
        }
        if (apiKey.length() <= 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "***";
    }
}
