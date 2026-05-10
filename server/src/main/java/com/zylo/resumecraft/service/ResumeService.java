package com.zylo.resumecraft.service;

import com.zylo.resumecraft.dto.ResumeCreateDTO;
import com.zylo.resumecraft.dto.ResumeTranslateCopyDTO;
import com.zylo.resumecraft.dto.ResumeUpdateDTO;
import com.zylo.resumecraft.entity.Resume;
import com.zylo.resumecraft.vo.ResumeListVO;

import java.util.List;

public interface ResumeService {
    List<ResumeListVO> listByUserId(Long userId);
    ResumeListVO create(Long userId, ResumeCreateDTO dto);
    ResumeListVO update(Long userId, Long resumeId, ResumeUpdateDTO dto);
    void delete(Long userId, Long resumeId);
    Resume getByIdAndUserId(Long resumeId, Long userId);
    ResumeListVO translateCopy(Long userId, Long resumeId, ResumeTranslateCopyDTO dto);
}

