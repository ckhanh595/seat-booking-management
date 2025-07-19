package com.concert.seatbooking.service;

import com.concert.seatbooking.entity.SeatTypeEntity;
import com.concert.seatbooking.exception.ValidationException;
import com.concert.seatbooking.repository.SeatTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatTypeService {

    public static final String DEFAULT_SEAT_TYPE_CODE = "00";
    public static final String DEFAULT_SEAT_TYPE_NAME = "New Seat Type";
    public static final String DEFAULT_WORKER_MEMO = "";

    private final SeatTypeRepository seatTypeRepository;

    @Transactional
    public SeatTypeEntity createSeatType(Authentication authentication) {
        log.info("Creating new seat type by user: {}", authentication.getName());

        var nextCode = generateNextSeatTypeCode();
        var seatType = SeatTypeEntity.defaultBuilder()
                .seatTypeCode(nextCode)
                .buildDefault();

        var savedSeatType = seatTypeRepository.save(seatType);
        log.info("Created new seat type 'New Seat Type' with code: {} by user: {}",
                nextCode, authentication.getName());

        return savedSeatType;
    }

    private String generateNextSeatTypeCode() {
        var maxCode = seatTypeRepository.findMaxSeatTypeCode().orElse(DEFAULT_SEAT_TYPE_CODE);

        try {
            var nextCodeInt = Integer.parseInt(maxCode) + 1;
            if (nextCodeInt > 99) {
                throw new ValidationException("Maximum code limit reached (99).");
            }
            return String.format("%02d", nextCodeInt);
        } catch (NumberFormatException e) {
            log.error("Invalid seat type code format found: {}", maxCode);
            throw e;
        }
    }
}