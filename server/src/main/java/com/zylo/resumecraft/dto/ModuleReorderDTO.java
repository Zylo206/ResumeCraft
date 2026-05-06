package com.zylo.resumecraft.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ModuleReorderDTO {
    @NotEmpty(message = "moduleIds cannot be empty")
    private List<@NotNull Long> moduleIds;
}

