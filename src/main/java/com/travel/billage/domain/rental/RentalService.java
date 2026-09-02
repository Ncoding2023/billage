package com.travel.billage.domain.rental;

import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.item.ItemRepository;
import com.travel.billage.domain.item.ItemStatus;
import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import com.travel.billage.domain.point.PointService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ItemReturnRepository itemReturnRepository;
    private final PointService pointService;

    @Transactional
    public Rental requestRental(Long memberNo, Long itemNo, LocalDate rentalStartDate, LocalDate rentalEndDate) {
        Member renter = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Item item = itemRepository.findById(itemNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 물품입니다."));

        if (item.getItemStatus() != ItemStatus.AVAILABLE) {
            throw new IllegalStateException("현재 대여할 수 없는 물품입니다.");
        }

        if (rentalRepository.existsOverlappingRental(item, rentalStartDate, rentalEndDate)) {
            throw new IllegalStateException("해당 기간에 이미 대여 예정이 있는 물품입니다.");
        }

        Rental rental = Rental.builder()
                .item(item)
                .member(renter)
                .rentalStartDate(rentalStartDate)
                .rentalEndDate(rentalEndDate)
                .rentalPoint(item.getRentalPoint())
                .build();

        rentalRepository.save(rental);

        // 대여 신청 시에는 대여자의 포인트만 차감한다.
        pointService.chargeForRental(renter, rental);

        return rental;
    }

    @Transactional
    public void startRental(Long rentalNo, Long providerMemberNo) {
        Rental rental = getRental(rentalNo);

        validateProvider(rental, providerMemberNo);

        if (rental.getRentalStatus() != RentalStatus.REQUESTED) {
            throw new IllegalStateException("대여신청 상태에서만 대여를 시작할 수 있습니다.");
        }

        // 제공자가 대여를 승인한 시점에 제공자에게 포인트를 지급한다.
        pointService.payoutForRental(
                rental.getItem().getMember(),
                rental
        );

        rental.changeStatus(RentalStatus.RENTING);
    }

    @Transactional
    public void cancelRental(Long rentalNo, Long memberNo) {
        Rental rental = getRental(rentalNo);

        if (!rental.getMember().getMemberNo().equals(memberNo)) {
            throw new IllegalStateException("본인의 대여만 취소할 수 있습니다.");
        }

        if (rental.getRentalStatus() != RentalStatus.REQUESTED) {
            throw new IllegalStateException("대여신청 상태에서만 취소할 수 있습니다.");
        }

        rental.changeStatus(RentalStatus.CANCELED);

        // 신청 단계에서는 제공자에게 포인트를 지급하지 않았으므로
        // 대여자의 차감 포인트만 환불한다.
        pointService.refundForRental(
                rental.getMember(),
                rental
        );
    }

    @Transactional
    public void requestReturn(Long rentalNo, Long memberNo) {
        Rental rental = getRental(rentalNo);

        if (!rental.getMember().getMemberNo().equals(memberNo)) {
            throw new IllegalStateException("본인의 대여만 반납 처리할 수 있습니다.");
        }

        if (rental.getRentalStatus() != RentalStatus.RENTING) {
            throw new IllegalStateException("대여중 상태에서만 반납할 수 있습니다.");
        }

        itemReturnRepository.save(ItemReturn.builder()
                .rental(rental)
                .returnDate(LocalDateTime.now())
                .returnStatus(ReturnStatus.NORMAL)
                .build());

        rental.changeStatus(RentalStatus.RETURN_PENDING);
    }

    @Transactional
    public void confirmReturn(Long rentalNo, Long providerMemberNo,
                              ReturnStatus returnStatus, String returnMemo) {
        Rental rental = getRental(rentalNo);

        validateProvider(rental, providerMemberNo);

        if (rental.getRentalStatus() != RentalStatus.RETURN_PENDING) {
            throw new IllegalStateException("반납대기 상태에서만 반납 확인을 할 수 있습니다.");
        }

        ItemReturn itemReturn = itemReturnRepository.findByRental(rental)
                .orElseThrow(() -> new IllegalStateException("반납 정보가 존재하지 않습니다."));

        itemReturn.confirmReturn(returnStatus, returnMemo);

        rental.changeStatus(RentalStatus.RETURN_COMPLETED);
    }

    public Rental getRental(Long rentalNo) {
        return rentalRepository.findById(rentalNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대여입니다."));
    }

    public List<Rental> getRentalsAsRenter(Long memberNo) {
        return rentalRepository.findByMember(
                memberRepository.findById(memberNo)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."))
        );
    }

    public List<Rental> getRentalsAsProvider(Long memberNo) {
        return rentalRepository.findByItem_Member(
                memberRepository.findById(memberNo)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."))
        );
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    private void validateProvider(Rental rental, Long providerMemberNo) {
        if (!rental.getItem().getMember().getMemberNo().equals(providerMemberNo)) {
            throw new IllegalStateException("물품 제공자만 처리할 수 있습니다.");
        }
    }
}