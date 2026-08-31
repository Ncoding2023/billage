package com.travel.billage.domain.inquiry;

import com.travel.billage.domain.inquiry.dto.InquiryCreateRequest;
import com.travel.billage.domain.inquiry.dto.InquiryResponse;
import com.travel.billage.domain.inquiry.dto.InquiryStatusUpdateRequest;
import com.travel.billage.security.MemberDetails;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<InquiryResponse> createInquiry(@AuthenticationPrincipal MemberDetails principal,
                                                           @Valid @RequestBody InquiryCreateRequest request) {
        Inquiry inquiry = inquiryService.createInquiry(
                principal.getMemberNo(), request.inquiryType(), request.inquiryContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(InquiryResponse.from(inquiry));
    }

    @GetMapping("/mine")
    public List<InquiryResponse> getMyInquiries(@AuthenticationPrincipal MemberDetails principal) {
        return inquiryService.getMyInquiries(principal.getMemberNo()).stream()
                .map(InquiryResponse::from)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<InquiryResponse> getInquiriesByStatus(@RequestParam InquiryStatus status) {
        return inquiryService.getInquiriesByStatus(status).stream()
                .map(InquiryResponse::from)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{inquiryNo}/status")
    public ResponseEntity<Void> changeProcessStatus(@PathVariable Long inquiryNo,
                                                      @Valid @RequestBody InquiryStatusUpdateRequest request) {
        inquiryService.changeProcessStatus(inquiryNo, request.processStatus());
        return ResponseEntity.noContent().build();
    }
}
