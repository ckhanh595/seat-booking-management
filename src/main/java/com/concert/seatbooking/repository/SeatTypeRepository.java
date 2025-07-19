package com.concert.seatbooking.repository;

import com.concert.seatbooking.entity.SeatTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatTypeEntity, Long> {

    @Query("SELECT MAX(s.seatTypeCode) FROM SeatTypeEntity s")
    Optional<String> findMaxSeatTypeCode();

    List<SeatTypeEntity> findAllByOrderBySeatTypeCodeAsc();

    Optional<SeatTypeEntity> findByIdAndDeletedFalse(Long id);

}