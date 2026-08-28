package com.sping.billage.domain.rental.entity;

import com.sping.billage.domain.item.entity.Item;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.rental.enums.RentalStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "RENTAL")
@SequenceGenerator(name = "RENTAL_SEQ_GEN", sequenceName = "RENTAL_SEQ", allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RENTAL_SEQ_GEN")
    @Column(name = "RENTAL_ID")
    private Long id;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "END_DATE", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 200, nullable = false)
    private RentalStatus status;

    @Column(name = "REQUESTED_AT")
    private LocalDateTime requestedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RENTER_ID", nullable = false)
    private Member renter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item;

    @Builder
    private Rental(LocalDate startDate, LocalDate endDate, Member renter, Item item) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.renter = renter;
        this.item = item;
        this.status = RentalStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
    }

    public void changeStatus(RentalStatus status) {
        this.status = status;
    }

    public boolean isRentedBy(Long memberId) {
        return this.renter.getId().equals(memberId);
    }

    public boolean isItemOwnedBy(Long memberId) {
        return this.item.isOwnedBy(memberId);
    }
}
