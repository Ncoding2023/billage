package com.travel.billage.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRole;
import com.travel.billage.domain.rental.Rental;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;

    @Test
    void getBalance_delegatesToRepository() {
        Member member = createMember();
        when(pointHistoryRepository.sumPointAmountByMember(member)).thenReturn(3000);

        assertThat(pointService.getBalance(member)).isEqualTo(3000);
    }

    @Test
    void grantSignupBonus_savesPositiveAmount() {
        Member member = createMember();
        ArgumentCaptor<PointHistory> captor = ArgumentCaptor.forClass(PointHistory.class);

        pointService.grantSignupBonus(member);

        verify(pointHistoryRepository).save(captor.capture());
        PointHistory saved = captor.getValue();
        assertThat(saved.getMember()).isEqualTo(member);
        assertThat(saved.getRental()).isNull();
        assertThat(saved.getPointAmount()).isEqualTo(5000);
        assertThat(saved.getPointType()).isEqualTo(PointType.SIGNUP_BONUS);
    }

    @Test
    void chargeForRental_sufficientBalance_savesNegativeAmount() {
        Member renter = createMember();
        Rental rental = createRental(renter, 1000);
        when(pointHistoryRepository.sumPointAmountByMember(renter)).thenReturn(5000);
        ArgumentCaptor<PointHistory> captor = ArgumentCaptor.forClass(PointHistory.class);

        pointService.chargeForRental(renter, rental);

        verify(pointHistoryRepository).save(captor.capture());
        PointHistory saved = captor.getValue();
        assertThat(saved.getPointAmount()).isEqualTo(-1000);
        assertThat(saved.getPointType()).isEqualTo(PointType.RENTAL_PAYMENT);
        assertThat(saved.getRental()).isEqualTo(rental);
    }

    @Test
    void chargeForRental_insufficientBalance_throws() {
        Member renter = createMember();
        Rental rental = createRental(renter, 1000);
        when(pointHistoryRepository.sumPointAmountByMember(renter)).thenReturn(500);

        assertThatThrownBy(() -> pointService.chargeForRental(renter, rental))
                .isInstanceOf(IllegalStateException.class);

        verify(pointHistoryRepository, never()).save(any());
    }

    @Test
    void payoutForRental_savesPositiveAmount() {
        Member owner = createMember();
        Rental rental = createRental(owner, 1000);
        ArgumentCaptor<PointHistory> captor = ArgumentCaptor.forClass(PointHistory.class);

        pointService.payoutForRental(owner, rental);

        verify(pointHistoryRepository).save(captor.capture());
        PointHistory saved = captor.getValue();
        assertThat(saved.getPointAmount()).isEqualTo(1000);
        assertThat(saved.getPointType()).isEqualTo(PointType.RENTAL_INCOME);
    }

    @Test
    void refundForRental_savesPositiveAmount() {
        Member renter = createMember();
        Rental rental = createRental(renter, 1000);
        ArgumentCaptor<PointHistory> captor = ArgumentCaptor.forClass(PointHistory.class);

        pointService.refundForRental(renter, rental);

        verify(pointHistoryRepository).save(captor.capture());
        PointHistory saved = captor.getValue();
        assertThat(saved.getPointAmount()).isEqualTo(1000);
        assertThat(saved.getPointType()).isEqualTo(PointType.RENTAL_REFUND);
    }

    @Test
    void reverseForRental_savesNegativeAmount() {
        Member owner = createMember();
        Rental rental = createRental(owner, 1000);
        ArgumentCaptor<PointHistory> captor = ArgumentCaptor.forClass(PointHistory.class);

        pointService.reverseForRental(owner, rental);

        verify(pointHistoryRepository).save(captor.capture());
        PointHistory saved = captor.getValue();
        assertThat(saved.getPointAmount()).isEqualTo(-1000);
        assertThat(saved.getPointType()).isEqualTo(PointType.RENTAL_REFUND);
    }

    private Member createMember() {
        return Member.builder()
                .email("member@billage.com")
                .password("encoded")
                .name("회원")
                .nickname("member")
                .phone("010-1111-2222")
                .role(MemberRole.USER)
                .build();
    }

    private Rental createRental(Member member, Integer rentalPoint) {
        Item item = Item.builder()
                .member(member)
                .category(Category.TOOL)
                .itemName("전동드릴")
                .description("거의 새것")
                .rentalPoint(rentalPoint)
                .rentalPlaceName("장소")
                .rentalPlace("서울시")
                .rentalPlaceDetail(null)
                .latitude(37.0)
                .longitude(127.0)
                .build();
        return Rental.builder()
                .item(item)
                .member(member)
                .rentalStartDate(LocalDate.now())
                .rentalEndDate(LocalDate.now().plusDays(3))
                .rentalPoint(rentalPoint)
                .build();
    }
}
