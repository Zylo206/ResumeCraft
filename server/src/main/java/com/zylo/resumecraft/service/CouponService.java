package com.zylo.resumecraft.service;

import com.zylo.resumecraft.dto.CouponAdminDTO;
import com.zylo.resumecraft.dto.CouponQuoteDTO;
import com.zylo.resumecraft.entity.CouponCode;
import com.zylo.resumecraft.entity.FeedbackSubmission;

import java.util.List;

public interface CouponService {
    CouponQuoteDTO quote(String couponCode);

    CouponCode issueForFeedback(FeedbackSubmission submission);

    CouponCode getByFeedbackSubmissionId(Long submissionId);

    void resendCoupon(CouponCode couponCode);

    void invalidateFeedbackCoupon(Long submissionId);

    List<CouponAdminDTO> listCoupons();
}

