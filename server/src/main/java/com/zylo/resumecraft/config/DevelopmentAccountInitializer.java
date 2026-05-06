package com.zylo.resumecraft.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zylo.resumecraft.entity.User;
import com.zylo.resumecraft.entity.UserAuthIdentity;
import com.zylo.resumecraft.mapper.UserAuthIdentityMapper;
import com.zylo.resumecraft.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class DevelopmentAccountInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final UserAuthIdentityMapper userAuthIdentityMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.environment:development}")
    private String appEnvironment;

    @Value("${app.dev-account.email:test@example.com}")
    private String devAccountEmail;

    @Value("${app.dev-account.password:Test123456}")
    private String devAccountPassword;

    @Value("${app.dev-admin.email:admin@example.com}")
    private String devAdminEmail;

    @Value("${app.dev-admin.password:Admin123456}")
    private String devAdminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (!"development".equalsIgnoreCase(appEnvironment)) {
            return;
        }

        ensureAccount(devAccountEmail, devAccountPassword, 0, "Test User");
        ensureAccount(devAdminEmail, devAdminPassword, 1, "Admin User");
    }

    private void ensureAccount(String email, String password, int role, String nickname) {
        String normalizedEmail = email.trim().toLowerCase();
        var existingUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getEmail, normalizedEmail)
        );

        var encodedPassword = passwordEncoder.encode(password);
        var user = existingUser;
        if (user == null) {
            user = new User();
            user.setEmail(normalizedEmail);
            user.setPassword(encodedPassword);
            user.setNickname(nickname);
            user.setAvatar("");
            user.setRole(role);
            user.setStatus(1);
            user.setMembershipStatus(role == 1 ? "ACTIVE" : "FREE");
            userMapper.insert(user);
            log.info("Development account created: {}", normalizedEmail);
        } else {
            user.setEmail(normalizedEmail);
            user.setPassword(encodedPassword);
            user.setRole(role);
            user.setStatus(1);
            if (user.getNickname() == null || user.getNickname().isBlank()) {
                user.setNickname(nickname);
            }
            if (user.getAvatar() == null) {
                user.setAvatar("");
            }
            if (user.getMembershipStatus() == null || user.getMembershipStatus().isBlank()) {
                user.setMembershipStatus(role == 1 ? "ACTIVE" : "FREE");
            }
            userMapper.updateById(user);
            log.info("Development account repaired: {}", normalizedEmail);
        }

        var identity = userAuthIdentityMapper.selectOne(
                new LambdaQueryWrapper<UserAuthIdentity>()
                        .eq(UserAuthIdentity::getProvider, "EMAIL_PASSWORD")
                        .eq(UserAuthIdentity::getPrincipal, normalizedEmail)
                        .last("LIMIT 1")
        );

        if (identity == null) {
            identity = new UserAuthIdentity();
            identity.setUserId(user.getId());
            identity.setProvider("EMAIL_PASSWORD");
            identity.setPrincipal(normalizedEmail);
            identity.setVerifiedAt(LocalDateTime.now());
            identity.setStatus(1);
            identity.setCredentialHash(user.getPassword());
            userAuthIdentityMapper.insert(identity);
            return;
        }

        identity.setUserId(user.getId());
        identity.setPrincipal(normalizedEmail);
        identity.setCredentialHash(user.getPassword());
        identity.setStatus(1);
        if (identity.getVerifiedAt() == null) {
            identity.setVerifiedAt(LocalDateTime.now());
        }
        userAuthIdentityMapper.updateById(identity);
    }
}

