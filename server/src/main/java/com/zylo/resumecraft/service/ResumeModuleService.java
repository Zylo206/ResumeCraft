package com.zylo.resumecraft.service;

import com.zylo.resumecraft.dto.ModuleCreateDTO;
import com.zylo.resumecraft.dto.ModuleReorderDTO;
import com.zylo.resumecraft.dto.ModuleUpdateDTO;
import com.zylo.resumecraft.entity.ResumeModule;

import java.util.List;

public interface ResumeModuleService {
    List<ResumeModule> listByResumeId(Long resumeId, Long userId);
    ResumeModule create(Long resumeId, Long userId, ModuleCreateDTO dto);
    ResumeModule update(Long resumeId, Long userId, Long moduleId, ModuleUpdateDTO dto);
    List<ResumeModule> reorder(Long resumeId, Long userId, ModuleReorderDTO dto);
    void delete(Long resumeId, Long userId, Long moduleId);
}

