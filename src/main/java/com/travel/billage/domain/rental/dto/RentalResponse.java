package com.travel.billage.domain.rental.dto;

import com.travel.billage.domain.rental.Rental;
import com.travel.billage.domain.rental.RentalStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RentalResponse(
        Long rentalNo,
        Long itemNo,
        String itemName,
        Long memberNo,
        String memberNickname,
        String rentalPlaceName,
        String rentalPlace,
        String rentalPlaceDetail,
        Double latitude,
        Double longitude,
        LocalDate rentalStartDate,
        LocalDate rentalEndDate,
        Integer rentalPoint,
        RentalStatus rentalStatus,
        LocalDateTime rentalRequestDate
) {
    public static RentalResponse from(Rental rental) {
        return new RentalResponse(
                rental.getRentalNo(),
                rental.getItem().getItemNo(),
                rental.getItem().getItemName(),
                rental.getMember().getMemberNo(),
                rental.getMember().getNickname(),
                rental.getItem().getRentalPlaceName(),
                rental.getItem().getRentalPlace(),
                rental.getItem().getRentalPlaceDetail(),
                rental.getItem().getLatitude(),
                rental.getItem().getLongitude(),
                rental.getRentalStartDate(),
                rental.getRentalEndDate(),
                rental.getRentalPoint(),
                rental.getRentalStatus(),
                rental.getRentalRequestDate()
        );
    }
}
