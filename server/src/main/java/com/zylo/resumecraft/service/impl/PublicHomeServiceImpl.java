package com.zylo.resumecraft.service.impl;

import com.zylo.resumecraft.dto.HomeDTO;
import com.zylo.resumecraft.service.FeedbackSubmissionService;
import com.zylo.resumecraft.service.PlatformConfigService;
import com.zylo.resumecraft.service.PublicHomeService;
import com.zylo.resumecraft.service.ResumeShowcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicHomeServiceImpl implements PublicHomeService {
    private final PlatformConfigService platformConfigService;
    private final ResumeShowcaseService resumeShowcaseService;
    private final FeedbackSubmissionService feedbackSubmissionService;

    @Override
    public HomeDTO getHome() {
        HomeDTO dto = new HomeDTO();
        var config = platformConfigService.getConfig();
        dto.setMembershipPriceCents(config.getMembershipPriceCents());
        dto.setQuestionnaireCouponAmountCents(config.getQuestionnaireCouponAmountCents());
        dto.setShowcases(resumeShowcaseService.listPublishedShowcases());
        dto.setTestimonials(feedbackSubmissionService.listPublishedTestimonials());
        return dto;
    }
}

