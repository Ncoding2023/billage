package com.travel.billage.domain.rental;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "TB_ITEM_RETURN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ItemReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_no")
    private Long returnNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_no", nullable = false, unique = true)
    private Rental rental;

    @Column(name = "return_date", nullable = false)
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "return_status", nullable = false, length = 20)
    private ReturnStatus returnStatus;

    @Column(name = "return_memo", length = 500)
    private String returnMemo;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ItemReturn(Rental rental, LocalDateTime returnDate, ReturnStatus returnStatus, String returnMemo) {
        this.rental = rental;
        this.returnDate = returnDate;
        this.returnStatus = returnStatus;
        this.returnMemo = returnMemo;
    }

    public void confirmReturn(ReturnStatus returnStatus, String returnMemo) {
        this.returnStatus = returnStatus;
        this.returnMemo = returnMemo;
    }
}
