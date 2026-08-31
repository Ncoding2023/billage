package com.travel.billage.domain.rental;

import com.travel.billage.domain.rental.dto.RentalResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/rentals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RentalAdminController {

    private final RentalService rentalService;

    @GetMapping
    public List<RentalResponse> getAllRentals() {
        return rentalService.getAllRentals().stream()
                .map(RentalResponse::from)
                .toList();
    }
}
