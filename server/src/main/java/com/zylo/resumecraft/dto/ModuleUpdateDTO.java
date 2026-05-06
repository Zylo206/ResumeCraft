package com.zylo.resumecraft.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ModuleUpdateDTO {
    @NotNull(message = "模块内容不能为空")
    private Map<String, Object> content;
}

