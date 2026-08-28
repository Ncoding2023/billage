package com.sping.billage.domain.rental.repository;

import com.sping.billage.domain.rental.entity.ReturnInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnInfoRepository extends JpaRepository<ReturnInfo, Long> {

    Optional<ReturnInfo> findByRentalId(Long rentalId);

    boolean existsByRentalId(Long rentalId);
}
