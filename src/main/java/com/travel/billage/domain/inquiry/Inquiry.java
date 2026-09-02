package com.travel.billage.domain.inquiry;

import com.travel.billage.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "TB_INQUIRY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_no")
    private Long inquiryNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_type", nullable = false, length = 20)
    private InquiryType inquiryType;

    @Column(name = "inquiry_content", nullable = false, length = 2000)
    private String inquiryContent;

    @CreatedDate
    @Column(name = "inquiry_date", updatable = false)
    private LocalDateTime inquiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_status", nullable = false, length = 20)
    private InquiryStatus processStatus;

    @Column(name = "admin_comment", length = 1000)
    private String adminComment;

    @Builder
    public Inquiry(Member member, InquiryType inquiryType, String inquiryContent) {
        this.member = member;
        this.inquiryType = inquiryType;
        this.inquiryContent = inquiryContent;
        this.processStatus = InquiryStatus.RECEIVED;
    }

    public void changeProcessStatus(InquiryStatus processStatus, String adminComment) {
        this.processStatus = processStatus;
        this.adminComment = adminComment;
    }
}