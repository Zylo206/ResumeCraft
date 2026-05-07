package com.zylo.resumecraft.service.impl;

import com.zylo.resumecraft.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.local.enabled", havingValue = "true")
public class LocalMailServiceImpl implements MailService {

    @Override
    public void sendVerificationCode(String email, String code) {
        log.info("本地模式 - 验证码: {} -> {}", email, code);
    }

    @Override
    public void sendCouponCode(String email, String couponCode, int amountCents) {
        log.info("本地模式 - 优惠码: {} -> {}", email, couponCode);
    }
}
