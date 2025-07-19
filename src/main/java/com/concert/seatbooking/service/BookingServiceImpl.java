package com.concert.seatbooking.service;

import com.concert.seatbooking.entity.BookingEntity;
import com.concert.seatbooking.exception.NotFoundException;
import com.concert.seatbooking.exception.ValidationException;
import com.concert.seatbooking.mapper.SeatTypeMapper;
import com.concert.seatbooking.model.seattype.SeatTypeResponse;
import com.concert.seatbooking.repository.BookingRepository;
import com.concert.seatbooking.repository.SeatTypeRepository;
import com.concert.seatbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final SeatTypeRepository seatTypeRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public List<SeatTypeResponse> getAvailableSeatTypes() {
        var availableSeatTypes = seatTypeRepository.findByDeletedFalseAndIsBookedFalseOrderBySeatTypeCodeAsc();

        return SeatTypeMapper.toResponseList(availableSeatTypes);
    }

    @Transactional
    @Override
    public void bookSeatType(Long seatTypeId, Authentication authentication) {
        log.info("Booking seat type {} by user: {}", seatTypeId, authentication.getName());

        var seatType = seatTypeRepository.findByIdAndDeletedFalse(seatTypeId)
                .orElseThrow(() -> new NotFoundException("Seat type not found or has been deleted"));

        if (TRUE.equals(seatType.getIsBooked())) {
            throw new ValidationException("This seat type is already booked");
        }

        var user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        seatType.markAsBooked();
        var updatedSeatType = seatTypeRepository.save(seatType);

        var booking = BookingEntity.builder()
                .user(user)
                .seatType(updatedSeatType)
                .notes("Seat booked via booking page")
                .build();

        bookingRepository.save(booking);

        log.info("Successfully booked seat type {} for user: {}", seatTypeId, authentication.getName());
    }
}