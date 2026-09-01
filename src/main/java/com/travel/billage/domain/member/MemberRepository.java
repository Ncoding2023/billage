package com.travel.billage.domain.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByEmailAndName(String email, String name);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
