package com.travel.billage.domain.inquiry.dto;

import com.travel.billage.domain.inquiry.Inquiry;
import com.travel.billage.domain.inquiry.InquiryStatus;
import com.travel.billage.domain.inquiry.InquiryType;
import java.time.LocalDateTime;

public record InquiryResponse(
        Long inquiryNo,
        Long memberNo,
        InquiryType inquiryType,
        String inquiryContent,
        LocalDateTime inquiryDate,
        InquiryStatus processStatus,
        String adminComment
) {
    public static InquiryResponse from(Inquiry inquiry) {
        return new InquiryResponse(
                inquiry.getInquiryNo(),
                inquiry.getMember().getMemberNo(),
                inquiry.getInquiryType(),
                inquiry.getInquiryContent(),
                inquiry.getInquiryDate(),
                inquiry.getProcessStatus(),
                inquiry.getAdminComment()
        );
    }
}