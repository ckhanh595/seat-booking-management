package com.concert.seatbooking.service;

import com.concert.seatbooking.model.seattype.SeatTypeResponse;
import com.concert.seatbooking.model.seattype.UpdateSeatTypeRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface SeatTypeService {
    SeatTypeResponse createSeatType(Authentication authentication);

    List<SeatTypeResponse> getAllSeatTypes();

    long getTotalSeatTypeCount();

    SeatTypeResponse getSeatTypeById(Long id);

    SeatTypeResponse updateSeatType(Long id, UpdateSeatTypeRequest request, Authentication authentication);
}
