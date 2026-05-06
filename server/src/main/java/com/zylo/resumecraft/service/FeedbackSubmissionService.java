package com.zylo.resumecraft.service;

import com.zylo.resumecraft.dto.ApproveFeedbackSubmissionDTO;
import com.zylo.resumecraft.dto.FeedbackSubmissionAdminDTO;
import com.zylo.resumecraft.dto.FeedbackSubmissionCreateDTO;
import com.zylo.resumecraft.dto.PublishedFeedbackDTO;
import com.zylo.resumecraft.dto.RejectFeedbackSubmissionDTO;

import java.util.List;

public interface FeedbackSubmissionService {
    void submit(FeedbackSubmissionCreateDTO dto, String sourceIp);

    List<FeedbackSubmissionAdminDTO> listAdminSubmissions();

    FeedbackSubmissionAdminDTO approve(Long submissionId, Long adminUserId, ApproveFeedbackSubmissionDTO dto);

    FeedbackSubmissionAdminDTO reject(Long submissionId, Long adminUserId, RejectFeedbackSubmissionDTO dto);

    FeedbackSubmissionAdminDTO publish(Long submissionId, Long adminUserId);

    FeedbackSubmissionAdminDTO unpublish(Long submissionId, Long adminUserId);

    FeedbackSubmissionAdminDTO resendCoupon(Long submissionId, Long adminUserId);

    List<PublishedFeedbackDTO> listPublishedTestimonials();
}

