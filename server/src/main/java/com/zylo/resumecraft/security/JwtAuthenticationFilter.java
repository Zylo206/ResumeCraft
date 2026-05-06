package com.zylo.resumecraft.security;

import com.zylo.resumecraft.service.KeyValueStore;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final KeyValueStore keyValueStore;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, KeyValueStore keyValueStore) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.keyValueStore = keyValueStore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            var jti = jwtTokenProvider.getJtiFromToken(token);
            var isBlacklisted = keyValueStore.hasKey("blacklist:" + jti);

            if (!isBlacklisted) {
                var claims = jwtTokenProvider.parseToken(token);
                var userId = Long.parseLong(claims.getSubject());
                var email = claims.get("email", String.class);
                var role = claims.get("type", String.class);

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                var authentication = new UsernamePasswordAuthenticationToken(userId, email, authorities);
                authentication.setDetails(claims);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        var bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

