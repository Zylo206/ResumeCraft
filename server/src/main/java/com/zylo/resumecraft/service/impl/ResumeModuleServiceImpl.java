package com.zylo.resumecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zylo.resumecraft.common.BusinessException;
import com.zylo.resumecraft.common.ResultCode;
import com.zylo.resumecraft.dto.ModuleCreateDTO;
import com.zylo.resumecraft.dto.ModuleReorderDTO;
import com.zylo.resumecraft.dto.ModuleUpdateDTO;
import com.zylo.resumecraft.entity.Resume;
import com.zylo.resumecraft.entity.ResumeModule;
import com.zylo.resumecraft.mapper.ResumeMapper;
import com.zylo.resumecraft.mapper.ResumeModuleMapper;
import com.zylo.resumecraft.service.ResumeModuleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Service
public class ResumeModuleServiceImpl implements ResumeModuleService {
    private static final Set<String> SINGLETON_MODULE_TYPES = Set.of("basic_info", "skill", "job_intention");

    private final ResumeModuleMapper moduleMapper;
    private final ResumeMapper resumeMapper;

    public ResumeModuleServiceImpl(ResumeModuleMapper moduleMapper, ResumeMapper resumeMapper) {
        this.moduleMapper = moduleMapper;
        this.resumeMapper = resumeMapper;
    }

    @Override
    public List<ResumeModule> listByResumeId(Long resumeId, Long userId) {
        verifyResumeOwnership(resumeId, userId);
        return moduleMapper.selectList(
            new LambdaQueryWrapper<ResumeModule>()
                .eq(ResumeModule::getResumeId, resumeId)
                .orderByAsc(ResumeModule::getSortOrder)
                .orderByAsc(ResumeModule::getId)
        );
    }

    @Override
    public ResumeModule create(Long resumeId, Long userId, ModuleCreateDTO dto) {
        verifyResumeOwnership(resumeId, userId);
        validateSingletonModule(resumeId, dto.getModuleType());

        var module = new ResumeModule();
        module.setResumeId(resumeId);
        module.setModuleType(dto.getModuleType());
        module.setContent(dto.getContent());
        module.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : getNextSortOrder(resumeId));
        moduleMapper.insert(module);
        touchResume(resumeId);
        return module;
    }

    @Override
    public ResumeModule update(Long resumeId, Long userId, Long moduleId, ModuleUpdateDTO dto) {
        verifyResumeOwnership(resumeId, userId);

        var module = moduleMapper.selectById(moduleId);
        if (module == null || !module.getResumeId().equals(resumeId)) {
            throw new BusinessException(ResultCode.MODULE_NOT_FOUND);
        }

        module.setContent(dto.getContent());
        moduleMapper.updateById(module);
        touchResume(resumeId);
        return module;
    }

    @Override
    public List<ResumeModule> reorder(Long resumeId, Long userId, ModuleReorderDTO dto) {
        verifyResumeOwnership(resumeId, userId);

        var requestedIds = dto.getModuleIds();
        var requestedIdSet = new HashSet<>(requestedIds);
        if (requestedIdSet.size() != requestedIds.size()) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }

        var modules = moduleMapper.selectList(
            new LambdaQueryWrapper<ResumeModule>()
                .eq(ResumeModule::getResumeId, resumeId)
        );
        var existingIds = modules.stream()
            .map(ResumeModule::getId)
            .collect(java.util.stream.Collectors.toSet());

        if (!existingIds.containsAll(requestedIds)) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }

        var orderedIds = new java.util.ArrayList<>(requestedIds);
        modules.stream()
            .filter(module -> !requestedIdSet.contains(module.getId()))
            .sorted(java.util.Comparator
                .comparing((ResumeModule module) -> module.getSortOrder() == null ? Integer.MAX_VALUE : module.getSortOrder())
                .thenComparing(ResumeModule::getId))
            .map(ResumeModule::getId)
            .forEach(orderedIds::add);

        for (int index = 0; index < orderedIds.size(); index++) {
            var module = new ResumeModule();
            module.setId(orderedIds.get(index));
            module.setSortOrder(index);
            moduleMapper.updateById(module);
        }

        touchResume(resumeId);
        return listByResumeId(resumeId, userId);
    }

    @Override
    public void delete(Long resumeId, Long userId, Long moduleId) {
        verifyResumeOwnership(resumeId, userId);

        var module = moduleMapper.selectById(moduleId);
        if (module == null || !module.getResumeId().equals(resumeId)) {
            throw new BusinessException(ResultCode.MODULE_NOT_FOUND);
        }
        moduleMapper.deleteById(moduleId);
        touchResume(resumeId);
    }

    private void verifyResumeOwnership(Long resumeId, Long userId) {
        var resume = resumeMapper.selectById(resumeId);
        if (resume == null || !resume.getUserId().equals(userId) || resume.getStatus() == 0) {
            throw new BusinessException(ResultCode.RESUME_NOT_FOUND);
        }
    }

    private void validateSingletonModule(Long resumeId, String moduleType) {
        if (!SINGLETON_MODULE_TYPES.contains(moduleType)) {
            return;
        }

        var existingCount = moduleMapper.selectCount(
            new LambdaQueryWrapper<ResumeModule>()
                .eq(ResumeModule::getResumeId, resumeId)
                .eq(ResumeModule::getModuleType, moduleType)
        );

        if (existingCount != null && existingCount > 0) {
            throw new BusinessException(ResultCode.MODULE_ALREADY_EXISTS);
        }
    }

    private int getNextSortOrder(Long resumeId) {
        var latestModule = moduleMapper.selectOne(
            new LambdaQueryWrapper<ResumeModule>()
                .eq(ResumeModule::getResumeId, resumeId)
                .orderByDesc(ResumeModule::getSortOrder)
                .orderByDesc(ResumeModule::getId)
                .last("LIMIT 1")
        );

        if (latestModule == null || latestModule.getSortOrder() == null) {
            return 1;
        }

        return latestModule.getSortOrder() + 1;
    }

    private void touchResume(Long resumeId) {
        var resume = new Resume();
        resume.setId(resumeId);
        resume.setUpdatedAt(LocalDateTime.now());
        resumeMapper.updateById(resume);
    }
}

