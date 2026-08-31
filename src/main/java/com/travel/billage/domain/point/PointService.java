package com.travel.billage.domain.point;

import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.rental.Rental;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private static final int SIGNUP_BONUS_POINT = 5000;

    private final PointHistoryRepository pointHistoryRepository;

    public int getBalance(Member member) {
        return pointHistoryRepository.sumPointAmountByMember(member);
    }

    @Transactional
    public void grantSignupBonus(Member member) {
        pointHistoryRepository.save(PointHistory.builder()
                .member(member)
                .pointAmount(SIGNUP_BONUS_POINT)
                .pointType(PointType.SIGNUP_BONUS)
                .pointContent("회원가입 축하 포인트 지급")
                .build());
    }

    @Transactional
    public void chargeForRental(Member renter, Rental rental) {
        if (getBalance(renter) < rental.getRentalPoint()) {
            throw new IllegalStateException("보유 포인트가 부족합니다.");
        }
        pointHistoryRepository.save(PointHistory.builder()
                .member(renter)
                .rental(rental)
                .pointAmount(-rental.getRentalPoint())
                .pointType(PointType.RENTAL_PAYMENT)
                .pointContent(rental.getItem().getItemName() + " 대여 포인트 차감")
                .build());
    }

    @Transactional
    public void payoutForRental(Member owner, Rental rental) {
        pointHistoryRepository.save(PointHistory.builder()
                .member(owner)
                .rental(rental)
                .pointAmount(rental.getRentalPoint())
                .pointType(PointType.RENTAL_INCOME)
                .pointContent(rental.getItem().getItemName() + " 대여 포인트 지급")
                .build());
    }

    @Transactional
    public void refundForRental(Member renter, Rental rental) {
        pointHistoryRepository.save(PointHistory.builder()
                .member(renter)
                .rental(rental)
                .pointAmount(rental.getRentalPoint())
                .pointType(PointType.RENTAL_REFUND)
                .pointContent(rental.getItem().getItemName() + " 대여 취소 환불")
                .build());
    }

    @Transactional
    public void reverseForRental(Member owner, Rental rental) {
        pointHistoryRepository.save(PointHistory.builder()
                .member(owner)
                .rental(rental)
                .pointAmount(-rental.getRentalPoint())
                .pointType(PointType.RENTAL_REFUND)
                .pointContent(rental.getItem().getItemName() + " 대여 취소로 인한 지급 회수")
                .build());
    }
}
