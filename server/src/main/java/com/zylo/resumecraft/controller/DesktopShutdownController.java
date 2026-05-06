package com.zylo.resumecraft.controller;

import com.zylo.resumecraft.config.DesktopLifecycleManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@ConditionalOnProperty(name = "app.local.enabled", havingValue = "true")
public class DesktopShutdownController {

    private final DesktopLifecycleManager lifecycleManager;

    public DesktopShutdownController(DesktopLifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }

    @GetMapping("/desktop/heartbeat")
    public Map<String, String> heartbeat() {
        lifecycleManager.recordHeartbeat();
        return Map.of("status", "ok");
    }

    @PostMapping("/desktop/shutdown")
    public Map<String, String> shutdown() {
        lifecycleManager.shutdown();
        return Map.of("status", "ok");
    }
}

