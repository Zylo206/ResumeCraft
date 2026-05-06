package com.zylo.resumecraft.service;

public interface MailService {
    void sendVerificationCode(String email, String code);

    void sendCouponCode(String email, String couponCode, int amountCents);
}

