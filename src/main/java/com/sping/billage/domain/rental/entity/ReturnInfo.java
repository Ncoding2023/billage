package com.sping.billage.domain.rental.entity;

import com.sping.billage.domain.rental.enums.ReturnStatus;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "RETURN_INFO")
@SequenceGenerator(name = "RETURN_INFO_SEQ_GEN", sequenceName = "RETURN_INFO_SEQ", allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReturnInfo extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RETURN_INFO_SEQ_GEN")
    @Column(name = "RETURN_INFO_ID")
    private Long id;

    @Column(name = "RETURNED_AT")
    private LocalDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    private ReturnStatus status;

    @Lob
    @Column(name = "MEMO", columnDefinition = "CLOB")
    private String memo;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RENTAL_ID", nullable = false, unique = true)
    private Rental rental;

    @Builder
    private ReturnInfo(String memo, Rental rental) {
        this.memo = memo;
        this.rental = rental;
        this.status = ReturnStatus.PENDING;
        this.returnedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = ReturnStatus.COMPLETED;
        this.returnedAt = LocalDateTime.now();
    }

    public void reject(String memo) {
        this.status = ReturnStatus.REJECTED;
        if (memo != null) {
            this.memo = memo;
        }
    }
}
