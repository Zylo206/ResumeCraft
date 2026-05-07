package com.zylo.resumecraft.service;

import java.util.Map;

public interface AiConfigService {

    Map<String, String> getConfig();

    void updateConfig(Map<String, String> updates);

    String getApiKey();

    String getBaseUrl();

    String getModel();

    String getAnalysisModel();
}
