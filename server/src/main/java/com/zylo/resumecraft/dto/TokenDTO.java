package com.zylo.resumecraft.dto;

import lombok.Data;

@Data
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private UserInfoDTO userInfo;

    public TokenDTO(String accessToken, String refreshToken, Long expiresIn, UserInfoDTO userInfo) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.userInfo = userInfo;
    }
}

