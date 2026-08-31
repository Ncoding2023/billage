package com.travel.billage.domain.rental;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemReturnRepository extends JpaRepository<ItemReturn, Long> {

    Optional<ItemReturn> findByRental(Rental rental);
}
