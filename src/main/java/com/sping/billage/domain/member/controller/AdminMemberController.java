package com.sping.billage.domain.member.controller;

import com.sping.billage.domain.member.dto.MemberResponse;
import com.sping.billage.domain.member.service.MemberService;
import com.sping.billage.global.common.ApiResponse;
import com.sping.billage.global.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin - Member", description = "관리자 회원 관리")
@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 전체 조회 (ADMIN)")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MemberResponse>>> getMembers(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(memberService.getMembers(pageable)));
    }
}
