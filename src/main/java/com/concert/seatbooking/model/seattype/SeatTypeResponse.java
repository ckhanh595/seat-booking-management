package com.concert.seatbooking.model.seattype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatTypeResponse {
    Long id;
    String seatTypeCode;
    String seatTypeName;
    String workerMemo;
    Boolean deleted;
    Boolean isBooked;
    Integer version;
}