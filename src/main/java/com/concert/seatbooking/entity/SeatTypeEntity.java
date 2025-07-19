package com.concert.seatbooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "seat_types")
@Getter
@NoArgsConstructor
public class SeatTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_type_code", nullable = false, length = 2)
    private String seatTypeCode;

    @Column(name = "seat_type_name", nullable = false, length = 30)
    private String seatTypeName;

    @Column(name = "worker_memo", length = 500)
    private String workerMemo;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @Column(name = "is_booked", nullable = false)
    private Boolean isBooked;

    @Version
    private Integer version;

    @OneToMany(mappedBy = "seatType")
    private Set<BookingEntity> bookingEntities = new HashSet<>();

    @Builder
    public SeatTypeEntity(@NonNull String seatTypeCode,
                          @NonNull String seatTypeName,
                          String workerMemo) {
        this.seatTypeCode = seatTypeCode;
        this.seatTypeName = seatTypeName;
        this.workerMemo = workerMemo;
        this.deleted = false;
        this.isBooked = false;
    }

}