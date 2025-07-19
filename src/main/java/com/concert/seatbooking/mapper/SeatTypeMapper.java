package com.concert.seatbooking.mapper;

import com.concert.seatbooking.entity.SeatTypeEntity;
import com.concert.seatbooking.model.seattype.SeatTypeResponse;
import com.concert.seatbooking.model.seattype.UpdateSeatTypeRequest;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public final class SeatTypeMapper {

    public static SeatTypeResponse toResponse(@NonNull SeatTypeEntity entity) {
        return SeatTypeResponse.builder()
                .id(entity.getId())
                .seatTypeCode(entity.getSeatTypeCode())
                .seatTypeName(entity.getSeatTypeName())
                .workerMemo(entity.getWorkerMemo())
                .deleted(entity.getDeleted())
                .isBooked(entity.getIsBooked())
                .version(entity.getVersion())
                .build();
    }

    public static List<SeatTypeResponse> toResponseList(@NonNull List<SeatTypeEntity> entities) {
        return entities.stream()
                .map(SeatTypeMapper::toResponse)
                .toList();
    }

    public static UpdateSeatTypeRequest toUpdateRequest(@NonNull SeatTypeResponse seatTypeResponse) {
        return UpdateSeatTypeRequest.builder()
                .seatTypeName(seatTypeResponse.getSeatTypeName())
                .workerMemo(seatTypeResponse.getWorkerMemo())
                .build();
    }
}