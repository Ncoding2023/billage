package com.travel.billage.domain.rental;

import com.travel.billage.domain.rental.dto.RentalRequest;
import com.travel.billage.domain.rental.dto.RentalResponse;
import com.travel.billage.domain.rental.dto.ReturnConfirmRequest;
import com.travel.billage.security.MemberDetails;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping
    public ResponseEntity<RentalResponse> requestRental(@AuthenticationPrincipal MemberDetails principal,
                                                          @Valid @RequestBody RentalRequest request) {
        Rental rental = rentalService.requestRental(
                principal.getMemberNo(), request.itemNo(), request.rentalStartDate(), request.rentalEndDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(RentalResponse.from(rental));
    }

    @GetMapping("/{rentalNo}")
    public RentalResponse getRental(@PathVariable Long rentalNo) {
        return RentalResponse.from(rentalService.getRental(rentalNo));
    }

    @GetMapping
    public List<RentalResponse> getRentals(@AuthenticationPrincipal MemberDetails principal,
                                            @RequestParam(defaultValue = "renter") String role) {
        List<Rental> rentals = "provider".equalsIgnoreCase(role)
                ? rentalService.getRentalsAsProvider(principal.getMemberNo())
                : rentalService.getRentalsAsRenter(principal.getMemberNo());
        return rentals.stream().map(RentalResponse::from).toList();
    }

    @PatchMapping("/{rentalNo}/start")
    public RentalResponse startRental(@PathVariable Long rentalNo, @AuthenticationPrincipal MemberDetails principal) {
        rentalService.startRental(rentalNo, principal.getMemberNo());
        return RentalResponse.from(rentalService.getRental(rentalNo));
    }

    @PatchMapping("/{rentalNo}/cancel")
    public RentalResponse cancelRental(@PathVariable Long rentalNo, @AuthenticationPrincipal MemberDetails principal) {
        rentalService.cancelRental(rentalNo, principal.getMemberNo());
        return RentalResponse.from(rentalService.getRental(rentalNo));
    }

    @PatchMapping("/{rentalNo}/return")
    public RentalResponse requestReturn(@PathVariable Long rentalNo, @AuthenticationPrincipal MemberDetails principal) {
        rentalService.requestReturn(rentalNo, principal.getMemberNo());
        return RentalResponse.from(rentalService.getRental(rentalNo));
    }

    @PatchMapping("/{rentalNo}/return/confirm")
    public RentalResponse confirmReturn(@PathVariable Long rentalNo, @AuthenticationPrincipal MemberDetails principal,
                                         @Valid @RequestBody ReturnConfirmRequest request) {
        rentalService.confirmReturn(rentalNo, principal.getMemberNo(), request.returnStatus(), request.returnMemo());
        return RentalResponse.from(rentalService.getRental(rentalNo));
    }
}
