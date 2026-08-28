package com.sping.billage.domain.member.service;

import com.sping.billage.domain.member.dto.MemberResponse;
import com.sping.billage.domain.member.dto.MemberUpdateRequest;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.mapper.MemberMapper;
import com.sping.billage.domain.member.repository.MemberRepository;
import com.sping.billage.domain.point.dto.PointHistoryResponse;
import com.sping.billage.domain.point.dto.PointSummaryResponse;
import com.sping.billage.domain.point.mapper.PointHistoryMapper;
import com.sping.billage.domain.point.repository.PointHistoryRepository;
import com.sping.billage.domain.point.service.PointService;
import com.sping.billage.global.common.PageResponse;
import com.sping.billage.global.exception.BusinessException;
import com.sping.billage.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointHistoryMapper pointHistoryMapper;
    private final PointService pointService;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse getMyInfo(Long memberId) {
        Member member = getMember(memberId);
        return memberMapper.toResponse(member, pointService.getBalance(memberId));
    }

    @Transactional
    public MemberResponse updateMyInfo(Long memberId, MemberUpdateRequest request) {
        Member member = getMember(memberId);

        if (StringUtils.hasText(request.nickname()) && !request.nickname().equals(member.getNickname())) {
            if (memberRepository.existsByNickname(request.nickname())) {
                throw new BusinessException(ErrorCode.NICKNAME_DUPLICATED);
            }
            member.changeNickname(request.nickname());
        }

        if (StringUtils.hasText(request.password())) {
            member.changePassword(passwordEncoder.encode(request.password()));
        }

        return memberMapper.toResponse(member, pointService.getBalance(memberId));
    }

    public PointSummaryResponse getPointSummary(Long memberId, Pageable pageable) {
        PageResponse<PointHistoryResponse> histories = PageResponse.of(
                pointHistoryRepository.findByMemberIdOrderByIdDesc(memberId, pageable),
                pointHistoryMapper::toResponse);

        return new PointSummaryResponse(pointService.getBalance(memberId), histories);
    }

    public PageResponse<MemberResponse> getMembers(Pageable pageable) {
        return PageResponse.of(memberRepository.findAll(pageable),
                member -> memberMapper.toResponse(member, pointService.getBalance(member.getId())));
    }

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
