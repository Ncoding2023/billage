package com.travel.billage.config;

import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import com.travel.billage.domain.member.MemberRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountSeeder implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.seed.email}")
    private String adminEmail;

    @Value("${admin.seed.password}")
    private String adminPassword;

    @Value("${admin.seed.name}")
    private String adminName;

    @Value("${admin.seed.nickname}")
    private String adminNickname;

    @Value("${admin.seed.phone}")
    private String adminPhone;

    @Override
    public void run(ApplicationArguments args) {
        if (memberRepository.existsByEmail(adminEmail)) {
            return;
        }

        Member admin = Member.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .name(adminName)
                .nickname(adminNickname)
                .phone(adminPhone)
                .role(MemberRole.ADMIN)
                .build();
        memberRepository.save(admin);

        log.info("관리자 계정이 생성되었습니다: {}", adminEmail);
    }
}
