package com.sping.billage.domain.point.entity;

import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.rental.entity.Rental;
import com.sping.billage.global.common.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포인트 이력. amount 부호로 적립(+)/사용(-)을 구분한다.
 */
@Entity
@Getter
@Table(name = "POINT_HISTORY")
@SequenceGenerator(name = "POINT_HISTORY_SEQ_GEN", sequenceName = "POINT_HISTORY_SEQ", allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POINT_HISTORY_SEQ_GEN")
    @Column(name = "POINT_HISTORY_ID")
    private Long id;

    @Column(name = "POINT_AMOUNT", nullable = false)
    private Long amount;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RENTAL_ID")
    private Rental rental;

    @Builder
    private PointHistory(Long amount, String description, Member member, Rental rental) {
        this.amount = amount;
        this.description = description;
        this.member = member;
        this.rental = rental;
    }
}
