package com.concert.seatbooking.service;

import com.concert.seatbooking.entity.SeatTypeEntity;
import com.concert.seatbooking.exception.NotFoundException;
import com.concert.seatbooking.exception.ValidationException;
import com.concert.seatbooking.mapper.SeatTypeMapper;
import com.concert.seatbooking.model.seattype.SeatTypeResponse;
import com.concert.seatbooking.model.seattype.UpdateSeatTypeRequest;
import com.concert.seatbooking.repository.SeatTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatTypeServiceImpl implements SeatTypeService {

    public static final String DEFAULT_SEAT_TYPE_CODE = "00";
    public static final String DEFAULT_SEAT_TYPE_NAME = "New Seat Type";
    public static final String DEFAULT_WORKER_MEMO = "";

    private final SeatTypeRepository seatTypeRepository;

    @Transactional
    @Override
    public SeatTypeResponse createSeatType(Authentication authentication) {
        log.info("Creating new seat type by user: {}", authentication.getName());

        var nextCode = generateNextSeatTypeCode();
        var seatType = SeatTypeEntity.defaultBuilder()
                .seatTypeCode(nextCode)
                .buildDefault();

        var savedSeatType = seatTypeRepository.save(seatType);
        log.info("Created new seat type 'New Seat Type' with code: {} by user: {}",
                nextCode, authentication.getName());

        return SeatTypeMapper.toResponse(savedSeatType);
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

    @Override
    public List<SeatTypeResponse> getAllSeatTypes() {
        var entities = seatTypeRepository.findAllByOrderBySeatTypeCodeAsc();
        return SeatTypeMapper.toResponseList(entities);
    }

    @Override
    public long getTotalSeatTypeCount() {
        return seatTypeRepository.count();
    }

    @Override
    public SeatTypeResponse getSeatTypeById(Long id) {
        return seatTypeRepository.findByIdAndDeletedFalse(id)
                .map(SeatTypeMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Seat type not found or has been deleted"));
    }

    @Transactional
    @Override
    public SeatTypeResponse updateSeatType(Long id, UpdateSeatTypeRequest request, Authentication authentication) {
        log.info("Updating seat type {} by user: {}", id, authentication.getName());

        var entity = seatTypeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ValidationException("Seat type not found or has been deleted"));

        entity.update(request.getSeatTypeName(), Optional.ofNullable(request.getWorkerMemo()).orElse(""));
        var updatedEntity = seatTypeRepository.save(entity);

        log.info("Updated seat type {} with name '{}' by user: {}", id, request.getSeatTypeName(), authentication.getName());

        return SeatTypeMapper.toResponse(updatedEntity);
    }

}