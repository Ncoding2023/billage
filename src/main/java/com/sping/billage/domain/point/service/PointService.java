package com.sping.billage.domain.point.service;

import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.point.entity.PointHistory;
import com.sping.billage.domain.point.repository.PointHistoryRepository;
import com.sping.billage.domain.rental.entity.Rental;
import com.sping.billage.global.exception.BusinessException;
import com.sping.billage.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 포인트 적립/사용 이력을 관리한다. 잔액은 이력 합계로 계산한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;

    public long getBalance(Long memberId) {
        return pointHistoryRepository.sumAmountByMemberId(memberId);
    }

    @Transactional
    public PointHistory earn(Member member, long amount, String description, Rental rental) {
        if (amount <= 0) {
            throw new IllegalArgumentException("적립 포인트는 0보다 커야 합니다.");
        }
        return save(member, amount, description, rental);
    }

    /**
     * 포인트를 사용한다. amount 는 양수로 전달하며 이력에는 음수로 기록된다.
     */
    @Transactional
    public PointHistory use(Member member, long amount, String description, Rental rental) {
        if (amount < 0) {
            throw new IllegalArgumentException("사용 포인트는 0 이상이어야 합니다.");
        }
        if (getBalance(member.getId()) < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT);
        }
        return save(member, -amount, description, rental);
    }

    private PointHistory save(Member member, long amount, String description, Rental rental) {
        return pointHistoryRepository.save(PointHistory.builder()
                .member(member)
                .amount(amount)
                .description(description)
                .rental(rental)
                .build());
    }
}
