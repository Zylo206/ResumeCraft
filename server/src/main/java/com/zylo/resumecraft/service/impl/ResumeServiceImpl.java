package com.zylo.resumecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zylo.resumecraft.common.BusinessException;
import com.zylo.resumecraft.common.ResultCode;
import com.zylo.resumecraft.dto.ResumeCreateDTO;
import com.zylo.resumecraft.dto.ResumeUpdateDTO;
import com.zylo.resumecraft.entity.Resume;
import com.zylo.resumecraft.mapper.ResumeMapper;
import com.zylo.resumecraft.service.ResumeService;
import com.zylo.resumecraft.vo.ResumeListVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;

    @Value("${resume.max-count-per-user:20}")
    private int maxResumeCountPerUser;

    public ResumeServiceImpl(ResumeMapper resumeMapper) {
        this.resumeMapper = resumeMapper;
    }

    @Override
    public List<ResumeListVO> listByUserId(Long userId) {
        var resumes = resumeMapper.selectList(
            new LambdaQueryWrapper<Resume>()
                .eq(Resume::getUserId, userId)
                .eq(Resume::getStatus, 1)
                .orderByDesc(Resume::getUpdatedAt)
        );

        return resumes.stream().map(r -> {
            var vo = new ResumeListVO();
            vo.setId(r.getId());
            vo.setTitle(r.getTitle());
            vo.setTemplateId(r.getTemplateId());
            vo.setCreatedAt(r.getCreatedAt());
            vo.setUpdatedAt(r.getUpdatedAt());
            return vo;
        }).toList();
    }

    @Override
    public ResumeListVO create(Long userId, ResumeCreateDTO dto) {
        // 检查简历数量上限
        var count = resumeMapper.selectCount(
            new LambdaQueryWrapper<Resume>()
                .eq(Resume::getUserId, userId)
                .eq(Resume::getStatus, 1)
        );
        if (count >= maxResumeCountPerUser) {
            throw new BusinessException(ResultCode.RESUME_LIMIT_REACHED);
        }

        var resume = new Resume();
        resume.setUserId(userId);
        resume.setTitle(dto.getTitle() != null ? dto.getTitle() : "未命名简历");
        resume.setTemplateId(dto.getTemplateId() != null ? dto.getTemplateId() : "default");
        resume.setStatus(1);
        resumeMapper.insert(resume);

        var vo = new ResumeListVO();
        vo.setId(resume.getId());
        vo.setTitle(resume.getTitle());
        vo.setTemplateId(resume.getTemplateId());
        vo.setCreatedAt(resume.getCreatedAt());
        vo.setUpdatedAt(resume.getUpdatedAt());
        return vo;
    }

    @Override
    public ResumeListVO update(Long userId, Long resumeId, ResumeUpdateDTO dto) {
        var resume = getAndVerifyOwnership(resumeId, userId);
        resume.setTitle(dto.getTitle().trim());
        resumeMapper.updateById(resume);
        return toListVO(resume);
    }

    @Override
    public void delete(Long userId, Long resumeId) {
        getAndVerifyOwnership(resumeId, userId);
        resumeMapper.deleteById(resumeId);
    }

    @Override
    public Resume getByIdAndUserId(Long resumeId, Long userId) {
        return getAndVerifyOwnership(resumeId, userId);
    }

    private ResumeListVO toListVO(Resume resume) {
        var vo = new ResumeListVO();
        vo.setId(resume.getId());
        vo.setTitle(resume.getTitle());
        vo.setTemplateId(resume.getTemplateId());
        vo.setCreatedAt(resume.getCreatedAt());
        vo.setUpdatedAt(resume.getUpdatedAt());
        return vo;
    }

    private Resume getAndVerifyOwnership(Long resumeId, Long userId) {
        var resume = resumeMapper.selectById(resumeId);
        if (resume == null || !resume.getUserId().equals(userId) || resume.getStatus() == 0) {
            throw new BusinessException(ResultCode.RESUME_NOT_FOUND);
        }
        return resume;
    }
}

