package com.sping.billage.domain.point.service;

import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.enums.MemberRole;
import com.sping.billage.domain.point.entity.PointHistory;
import com.sping.billage.domain.point.repository.PointHistoryRepository;
import com.sping.billage.global.constant.PointPolicy;
import com.sping.billage.global.exception.BusinessException;
import com.sping.billage.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("포인트 적립은 양수 이력으로 기록된다")
    void earn_savesPositiveAmount() {
        Member member = member();
        given(pointHistoryRepository.save(any(PointHistory.class))).willAnswer(call -> call.getArgument(0));

        pointService.earn(member, PointPolicy.SIGNUP_BONUS, PointPolicy.DESC_SIGNUP_BONUS, null);

        ArgumentCaptor<PointHistory> captor = ArgumentCaptor.forClass(PointHistory.class);
        verify(pointHistoryRepository).save(captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualTo(PointPolicy.SIGNUP_BONUS);
        assertThat(captor.getValue().getDescription()).isEqualTo(PointPolicy.DESC_SIGNUP_BONUS);
        assertThat(captor.getValue().getRental()).isNull();
    }

    @Test
    @DisplayName("포인트 사용은 음수 이력으로 기록된다")
    void use_savesNegativeAmount() {
        Member member = member();
        given(pointHistoryRepository.sumAmountByMemberId(any())).willReturn(5000L);
        given(pointHistoryRepository.save(any(PointHistory.class))).willAnswer(call -> call.getArgument(0));

        pointService.use(member, 3000L, PointPolicy.DESC_RENTAL_USE, null);

        ArgumentCaptor<PointHistory> captor = ArgumentCaptor.forClass(PointHistory.class);
        verify(pointHistoryRepository).save(captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualTo(-3000L);
    }

    @Test
    @DisplayName("잔액이 부족하면 포인트를 사용할 수 없다")
    void use_insufficientBalance_throws() {
        Member member = member();
        given(pointHistoryRepository.sumAmountByMemberId(any())).willReturn(1000L);

        assertThatThrownBy(() -> pointService.use(member, 3000L, PointPolicy.DESC_RENTAL_USE, null))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INSUFFICIENT_POINT);

        verify(pointHistoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("잔액은 이력 합계로 계산한다")
    void getBalance_sumsHistory() {
        given(pointHistoryRepository.sumAmountByMemberId(1L)).willReturn(1500L);

        assertThat(pointService.getBalance(1L)).isEqualTo(1500L);
    }

    private Member member() {
        return Member.builder()
                .email("user@billage.com")
                .password("encoded")
                .nickname("빌리지유저")
                .role(MemberRole.USER)
                .build();
    }
}
