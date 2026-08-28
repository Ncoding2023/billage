package com.sping.billage.domain.place.repository;

import com.sping.billage.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByItemId(Long itemId);
}
