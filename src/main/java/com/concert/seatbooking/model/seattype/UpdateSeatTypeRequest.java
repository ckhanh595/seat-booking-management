package com.concert.seatbooking.model.seattype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Setter
public class UpdateSeatTypeRequest {

    @NotBlank(message = "Seat type name is required")
    @Size(max = 30, message = "Seat type name must not exceed 30 characters")
    private String seatTypeName;

    @Size(max = 500, message = "Worker memo must not exceed 500 characters")
    private String workerMemo;
}