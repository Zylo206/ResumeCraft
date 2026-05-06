package com.zylo.resumecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zylo.resumecraft.common.BusinessException;
import com.zylo.resumecraft.common.ResultCode;
import com.zylo.resumecraft.dto.CouponQuoteDTO;
import com.zylo.resumecraft.dto.UserAdminDTO;
import com.zylo.resumecraft.entity.User;
import com.zylo.resumecraft.mapper.UserMapper;
import com.zylo.resumecraft.service.CouponService;
import com.zylo.resumecraft.service.MembershipService;
import com.zylo.resumecraft.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    private final CouponService couponService;
    private final UserMapper userMapper;

    @Override
    public CouponQuoteDTO quote(String couponCode) {
        return couponService.quote(couponCode);
    }

    @Override
    public List<UserAdminDTO> listUsers() {
        return userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .orderByDesc(User::getCreatedAt)
                        .orderByDesc(User::getId)
        ).stream().map(this::toAdminDto).toList();
    }

    @Override
    public UserAdminDTO grantMembership(Long userId, Long adminUserId) {
        User user = getUser(userId);
        user.setMembershipStatus("ACTIVE");
        user.setMembershipGrantedAt(LocalDateTime.now());
        user.setMembershipSource("ADMIN_GRANTED");
        user.setMembershipExpiresAt(null);
        userMapper.updateById(user);
        return toAdminDto(user);
    }

    @Override
    public UserAdminDTO revokeMembership(Long userId, Long adminUserId) {
        User user = getUser(userId);
        user.setMembershipStatus("FREE");
        user.setMembershipGrantedAt(null);
        user.setMembershipSource("ADMIN_REVOKED");
        user.setMembershipExpiresAt(null);
        userMapper.updateById(user);
        return toAdminDto(user);
    }

    private User getUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    private UserAdminDTO toAdminDto(User user) {
        UserAdminDTO dto = new UserAdminDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setRole(user.getRole() != null && user.getRole() == 1 ? "ADMIN" : "USER");
        dto.setMembershipStatus(user.getMembershipStatus());
        dto.setMembershipGrantedAt(DateTimeUtils.format(user.getMembershipGrantedAt()));
        dto.setMembershipSource(user.getMembershipSource());
        dto.setCreatedAt(DateTimeUtils.format(user.getCreatedAt()));
        return dto;
    }
}

