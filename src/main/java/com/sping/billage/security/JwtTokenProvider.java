package com.sping.billage.security;

import com.sping.billage.domain.member.enums.MemberRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_NICKNAME = "nickname";

    private final SecretKey secretKey;
    private final long accessTokenValidityMillis;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.access-token-validity-seconds}") long accessTokenValiditySeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityMillis = accessTokenValiditySeconds * 1000L;
    }

    public String createAccessToken(Long memberId, String email, String nickname, MemberRole role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenValidityMillis);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim(CLAIM_ROLE, role.name())
                .claim(CLAIM_NICKNAME, nickname)
                .audience().add(email).and()
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public Long getMemberId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public MemberRole getRole(String token) {
        return MemberRole.valueOf(parseClaims(token).get(CLAIM_ROLE, String.class));
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("만료된 토큰");
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("유효하지 않은 토큰");
            return false;
        }
    }

    public long getAccessTokenValiditySeconds() {
        return accessTokenValidityMillis / 1000L;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
