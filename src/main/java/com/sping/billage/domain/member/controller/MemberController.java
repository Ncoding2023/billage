package com.sping.billage.domain.member.controller;

import com.sping.billage.domain.member.dto.MemberResponse;
import com.sping.billage.domain.member.dto.MemberUpdateRequest;
import com.sping.billage.domain.member.service.MemberService;
import com.sping.billage.domain.point.dto.PointSummaryResponse;
import com.sping.billage.global.common.ApiResponse;
import com.sping.billage.security.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "내 정보 / 포인트")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyInfo(
            @AuthenticationPrincipal MemberPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(memberService.getMyInfo(principal.getMemberId())));
    }

    @Operation(summary = "내 정보 수정", description = "닉네임 / 비밀번호를 변경한다.")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMyInfo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody MemberUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                memberService.updateMyInfo(principal.getMemberId(), request),
                "회원 정보가 수정되었습니다."));
    }

    @Operation(summary = "내 포인트 잔액 및 이력 조회")
    @GetMapping("/me/points")
    public ResponseEntity<ApiResponse<PointSummaryResponse>> getMyPoints(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                memberService.getPointSummary(principal.getMemberId(), pageable)));
    }
}
