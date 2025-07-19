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

import static com.concert.seatbooking.service.SeatTypeServiceImpl.DEFAULT_SEAT_TYPE_NAME;
import static com.concert.seatbooking.service.SeatTypeServiceImpl.DEFAULT_WORKER_MEMO;

@Entity
@Table(name = "seat_types")
@Getter
@NoArgsConstructor
public class SeatTypeEntity extends BaseEntity {

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

    @Builder(builderMethodName = "defaultBuilder", buildMethodName = "buildDefault")
    public SeatTypeEntity(@NonNull String seatTypeCode) {
        this.seatTypeCode = seatTypeCode;
        this.seatTypeName = DEFAULT_SEAT_TYPE_NAME;
        this.workerMemo = DEFAULT_WORKER_MEMO;
        this.deleted = false;
        this.isBooked = false;
    }

    public void update(@NonNull String seatTypeName, String workerMemo) {
        this.seatTypeName = seatTypeName;
        this.workerMemo = workerMemo;
    }
    
    public void markAsDeleted() {
        this.deleted = true;
    }
    
    public void markAsBooked() {
        this.isBooked = true;
    }
    
    @Builder(builderMethodName = "duplicateBuilder", buildMethodName = "buildDuplicate")
    public SeatTypeEntity(@NonNull String seatTypeCode, 
                          @NonNull String seatTypeName, 
                          String workerMemo) {
        this.seatTypeCode = seatTypeCode;
        this.seatTypeName = seatTypeName;
        this.workerMemo = workerMemo != null ? workerMemo : "";
        this.deleted = false;
        this.isBooked = false;
    }
}