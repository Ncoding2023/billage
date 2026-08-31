package com.travel.billage.domain.rental;

import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.point.PointHistory;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "TB_RENTAL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_no")
    private Long rentalNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_no", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;

    @Column(name = "rental_start_date", nullable = false)
    private LocalDate rentalStartDate;

    @Column(name = "rental_end_date", nullable = false)
    private LocalDate rentalEndDate;

    @Column(name = "rental_point", nullable = false)
    private Integer rentalPoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "rental_status", nullable = false, length = 20)
    private RentalStatus rentalStatus;

    @CreatedDate
    @Column(name = "rental_request_date", updatable = false)
    private LocalDateTime rentalRequestDate;

    @OneToOne(mappedBy = "rental")
    private ItemReturn itemReturn;

    @OneToMany(mappedBy = "rental")
    private List<PointHistory> pointHistories = new ArrayList<>();

    @Builder
    public Rental(Item item, Member member, LocalDate rentalStartDate, LocalDate rentalEndDate,
                  Integer rentalPoint) {
        this.item = item;
        this.member = member;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        this.rentalPoint = rentalPoint;
        this.rentalStatus = RentalStatus.REQUESTED;
    }

    public void changeStatus(RentalStatus rentalStatus) {
        this.rentalStatus = rentalStatus;
    }
}
