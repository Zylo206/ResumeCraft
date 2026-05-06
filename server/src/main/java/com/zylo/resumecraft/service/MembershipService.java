package com.zylo.resumecraft.service;

import com.zylo.resumecraft.dto.CouponQuoteDTO;
import com.zylo.resumecraft.dto.UserAdminDTO;

import java.util.List;

public interface MembershipService {
    CouponQuoteDTO quote(String couponCode);

    List<UserAdminDTO> listUsers();

    UserAdminDTO grantMembership(Long userId, Long adminUserId);

    UserAdminDTO revokeMembership(Long userId, Long adminUserId);
}

