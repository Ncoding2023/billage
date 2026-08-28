package com.sping.billage.domain.inquiry.entity;

import com.sping.billage.domain.inquiry.enums.InquiryStatus;
import com.sping.billage.domain.inquiry.enums.InquiryType;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.global.common.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "INQUIRY")
@SequenceGenerator(name = "INQUIRY_SEQ_GEN", sequenceName = "INQUIRY_SEQ", allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INQUIRY_SEQ_GEN")
    @Column(name = "INQUIRY_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 20, nullable = false)
    private InquiryType type;

    @Lob
    @Column(name = "CONTENT", columnDefinition = "CLOB", nullable = false)
    private String content;

    @Lob
    @Column(name = "ANSWER", columnDefinition = "CLOB")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    private InquiryStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @Builder
    private Inquiry(InquiryType type, String content, Member member) {
        this.type = type;
        this.content = content;
        this.member = member;
        this.status = InquiryStatus.WAITING;
    }

    public void answer(String answer) {
        this.answer = answer;
        this.status = InquiryStatus.ANSWERED;
    }

    public boolean isWrittenBy(Long memberId) {
        return this.member.getId().equals(memberId);
    }
}
