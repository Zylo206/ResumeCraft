package com.zylo.resumecraft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ModuleCreateDTO {
    @NotBlank(message = "模块类型不能为空")
    private String moduleType;

    @NotNull(message = "模块内容不能为空")
    private Map<String, Object> content;

    private Integer sortOrder;
}

