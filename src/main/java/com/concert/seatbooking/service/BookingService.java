package com.concert.seatbooking.service;

import com.concert.seatbooking.model.seattype.SeatTypeResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface BookingService {
    
    List<SeatTypeResponse> getAvailableSeatTypes();
    
    void bookSeatType(Long seatTypeId, Authentication authentication);
}