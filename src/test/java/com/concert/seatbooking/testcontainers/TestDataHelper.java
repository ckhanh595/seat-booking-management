package com.concert.seatbooking.testcontainers;

import com.concert.seatbooking.entity.SeatTypeEntity;
import com.concert.seatbooking.entity.UserEntity;
import com.concert.seatbooking.model.enums.UserRole;
import com.concert.seatbooking.repository.SeatTypeRepository;
import com.concert.seatbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

@Component
public class TestDataHelper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatTypeRepository seatTypeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity createTestUser(String username, UserRole role) {
        return createTestUser(username, role, username + "@test.com", username.toUpperCase() + " User");
    }

    public UserEntity createTestUser(String username, UserRole role, String email, String fullName) {
        UserEntity user = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode("password123"))
                .role(role)
                .email(email)
                .fullName(fullName)
                .build();
        
        return userRepository.save(user);
    }

    public SeatTypeEntity createTestSeatType(String seatTypeCode) {
        return createTestSeatType(seatTypeCode, "Test Seat " + seatTypeCode, "Test memo for " + seatTypeCode, false);
    }

    public SeatTypeEntity createTestSeatType(String seatTypeCode, String seatTypeName, String workerMemo, boolean isBooked) {
        SeatTypeEntity seatType = SeatTypeEntity.defaultBuilder()
                .seatTypeCode(seatTypeCode)
                .buildDefault();
        
        ReflectionTestUtils.setField(seatType, "seatTypeName", seatTypeName);
        ReflectionTestUtils.setField(seatType, "workerMemo", workerMemo);
        ReflectionTestUtils.setField(seatType, "deleted", false);
        ReflectionTestUtils.setField(seatType, "isBooked", isBooked);
        
        return seatTypeRepository.save(seatType);
    }

    public SeatTypeEntity createBookedSeatType(String seatTypeCode) {
        return createTestSeatType(seatTypeCode, "Booked Seat " + seatTypeCode, "Already booked", true);
    }

    public SeatTypeEntity createDeletedSeatType(String seatTypeCode) {
        SeatTypeEntity seatType = SeatTypeEntity.defaultBuilder()
                .seatTypeCode(seatTypeCode)
                .buildDefault();
        
        ReflectionTestUtils.setField(seatType, "seatTypeName", "Deleted Seat " + seatTypeCode);
        ReflectionTestUtils.setField(seatType, "workerMemo", "This seat is deleted");
        ReflectionTestUtils.setField(seatType, "deleted", true);
        ReflectionTestUtils.setField(seatType, "isBooked", false);
        
        return seatTypeRepository.save(seatType);
    }

    public List<SeatTypeEntity> createMultipleAvailableSeatTypes() {
        return List.of(
            createTestSeatType("A1", "Premium Seat A1", "VIP section", false),
            createTestSeatType("B1", "Standard Seat B1", "Regular section", false),
            createTestSeatType("C1", "Economy Seat C1", "Budget section", false)
        );
    }

    public void cleanupDatabase() {
        seatTypeRepository.deleteAll();
        userRepository.deleteAll();
    }

    public UserEntity getTestCustomer() {
        return createTestUser("customer", UserRole.CUSTOMER);
    }

    public UserEntity getTestStaff() {
        return createTestUser("staff", UserRole.STAFF);
    }

    public UserEntity getTestAdmin() {
        return createTestUser("admin", UserRole.SUPER_ADMIN);
    }
}