package com.zylo.resumecraft.service;

import com.zylo.resumecraft.dto.PlatformConfigDTO;
import com.zylo.resumecraft.entity.PlatformConfig;

public interface PlatformConfigService {
    PlatformConfigDTO getConfig();

    PlatformConfigDTO updateConfig(Long adminUserId, PlatformConfigDTO dto);

    PlatformConfig getConfigEntity();
}

