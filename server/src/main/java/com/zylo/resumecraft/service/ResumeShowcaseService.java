package com.zylo.resumecraft.service;

import com.zylo.resumecraft.dto.ShowcaseCardDTO;
import com.zylo.resumecraft.dto.ShowcaseDetailDTO;
import com.zylo.resumecraft.dto.ResumeShowcaseUpsertDTO;
import com.zylo.resumecraft.entity.ResumeShowcase;

import java.util.List;

public interface ResumeShowcaseService {
    List<ShowcaseCardDTO> listPublishedShowcases();

    ShowcaseDetailDTO getPublishedDetail(String slug);

    List<ResumeShowcase> listAdminShowcases();

    ResumeShowcase create(Long adminUserId, ResumeShowcaseUpsertDTO dto);

    ResumeShowcase update(Long showcaseId, Long adminUserId, ResumeShowcaseUpsertDTO dto);
}

