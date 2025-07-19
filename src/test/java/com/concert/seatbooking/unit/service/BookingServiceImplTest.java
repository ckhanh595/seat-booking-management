package com.concert.seatbooking.unit.service;

import com.concert.seatbooking.entity.BookingEntity;
import com.concert.seatbooking.entity.SeatTypeEntity;
import com.concert.seatbooking.entity.UserEntity;
import com.concert.seatbooking.exception.NotFoundException;
import com.concert.seatbooking.exception.ValidationException;
import com.concert.seatbooking.mapper.SeatTypeMapper;
import com.concert.seatbooking.model.enums.UserRole;
import com.concert.seatbooking.model.seattype.SeatTypeResponse;
import com.concert.seatbooking.repository.BookingRepository;
import com.concert.seatbooking.repository.SeatTypeRepository;
import com.concert.seatbooking.repository.UserRepository;
import com.concert.seatbooking.service.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingServiceImpl Unit Tests")
class BookingServiceImplTest {

    @Mock
    private SeatTypeRepository seatTypeRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private SeatTypeEntity seatTypeEntity;
    private UserEntity userEntity;
    private SeatTypeResponse seatTypeResponse;

    @BeforeEach
    void setUp() {
        seatTypeEntity = SeatTypeEntity.defaultBuilder()
                .seatTypeCode("A1")
                .buildDefault();
        
        ReflectionTestUtils.setField(seatTypeEntity, "id", 1L);
        ReflectionTestUtils.setField(seatTypeEntity, "seatTypeName", "Premium Seat");
        ReflectionTestUtils.setField(seatTypeEntity, "workerMemo", "Test memo");
        ReflectionTestUtils.setField(seatTypeEntity, "deleted", false);
        ReflectionTestUtils.setField(seatTypeEntity, "isBooked", false);
        ReflectionTestUtils.setField(seatTypeEntity, "version", 1);

        userEntity = UserEntity.builder()
                .username("testuser")
                .password("password")
                .role(UserRole.CUSTOMER)
                .email("test@example.com")
                .fullName("Test User")
                .build();
        
        ReflectionTestUtils.setField(userEntity, "id", 1L);
        ReflectionTestUtils.setField(userEntity, "version", 1);

        seatTypeResponse = SeatTypeResponse.builder()
                .id(1L)
                .seatTypeCode("A1")
                .seatTypeName("Premium Seat")
                .workerMemo("Test memo")
                .isBooked(false)
                .build();
    }

    @Nested
    @DisplayName("getAvailableSeatTypes() Tests")
    class GetAvailableSeatTypesTests {

        @Test
        @DisplayName("Should return available seat types successfully")
        void shouldReturnAvailableSeatTypesSuccessfully() {
            var availableSeatTypes = List.of(seatTypeEntity);
            var expectedResponses = List.of(seatTypeResponse);

            when(seatTypeRepository.findByDeletedFalseAndIsBookedFalseOrderBySeatTypeCodeAsc())
                    .thenReturn(availableSeatTypes);

            try (var mapperMock = mockStatic(SeatTypeMapper.class)) {
                mapperMock.when(() -> SeatTypeMapper.toResponseList(availableSeatTypes))
                        .thenReturn(expectedResponses);

                var result = bookingService.getAvailableSeatTypes();

                assertThat(result).isNotNull();
                assertThat(result).hasSize(1);
                assertThat(result.get(0)).isEqualTo(seatTypeResponse);

                verify(seatTypeRepository).findByDeletedFalseAndIsBookedFalseOrderBySeatTypeCodeAsc();
                mapperMock.verify(() -> SeatTypeMapper.toResponseList(availableSeatTypes));
            }
        }

        @Test
        @DisplayName("Should return empty list when no available seat types")
        void shouldReturnEmptyListWhenNoAvailableSeatTypes() {
            List<SeatTypeEntity> emptySeatTypes = Collections.emptyList();
            List<SeatTypeResponse> emptyResponses = Collections.emptyList();

            when(seatTypeRepository.findByDeletedFalseAndIsBookedFalseOrderBySeatTypeCodeAsc())
                    .thenReturn(emptySeatTypes);

            try (var mapperMock = mockStatic(SeatTypeMapper.class)) {
                mapperMock.when(() -> SeatTypeMapper.toResponseList(emptySeatTypes))
                        .thenReturn(emptyResponses);

                var result = bookingService.getAvailableSeatTypes();

                assertThat(result).isNotNull();
                assertThat(result).isEmpty();

                verify(seatTypeRepository).findByDeletedFalseAndIsBookedFalseOrderBySeatTypeCodeAsc();
                mapperMock.verify(() -> SeatTypeMapper.toResponseList(emptySeatTypes));
            }
        }
    }

    @Nested
    @DisplayName("bookSeatType() Success Tests")
    class BookSeatTypeSuccessTests {

        @Test
        @DisplayName("Should book seat type successfully")
        void shouldBookSeatTypeSuccessfully() {
            Long seatTypeId = 1L;
            var username = "testuser";

            when(authentication.getName()).thenReturn(username);
            when(seatTypeRepository.findByIdAndDeletedFalse(seatTypeId))
                    .thenReturn(Optional.of(seatTypeEntity));
            when(userRepository.findByUsername(username))
                    .thenReturn(Optional.of(userEntity));
            when(seatTypeRepository.save(any(SeatTypeEntity.class)))
                    .thenReturn(seatTypeEntity);
            when(bookingRepository.save(any(BookingEntity.class)))
                    .thenReturn(mock(BookingEntity.class));

            bookingService.bookSeatType(seatTypeId, authentication);

            verify(seatTypeRepository).findByIdAndDeletedFalse(seatTypeId);
            verify(userRepository).findByUsername(username);
            verify(seatTypeRepository).save(seatTypeEntity);
            verify(bookingRepository).save(any(BookingEntity.class));
            verify(authentication, times(3)).getName();
        }

        @Test
        @DisplayName("Should mark seat type as booked when booking")
        void shouldMarkSeatTypeAsBookedWhenBooking() {
            Long seatTypeId = 1L;
            var username = "testuser";
            var spySeatType = spy(seatTypeEntity);

            when(authentication.getName()).thenReturn(username);
            when(seatTypeRepository.findByIdAndDeletedFalse(seatTypeId))
                    .thenReturn(Optional.of(spySeatType));
            when(userRepository.findByUsername(username))
                    .thenReturn(Optional.of(userEntity));
            when(seatTypeRepository.save(any(SeatTypeEntity.class)))
                    .thenReturn(spySeatType);
            when(bookingRepository.save(any(BookingEntity.class)))
                    .thenReturn(mock(BookingEntity.class));

            bookingService.bookSeatType(seatTypeId, authentication);

            verify(spySeatType).markAsBooked();
        }
    }

    @Nested
    @DisplayName("bookSeatType() Exception Tests")
    class BookSeatTypeExceptionTests {

        @Test
        @DisplayName("Should throw NotFoundException when seat type not found")
        void shouldThrowNotFoundExceptionWhenSeatTypeNotFound() {
            Long seatTypeId = 999L;
            var username = "testuser";

            when(authentication.getName()).thenReturn(username);
            when(seatTypeRepository.findByIdAndDeletedFalse(seatTypeId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.bookSeatType(seatTypeId, authentication))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Seat type not found or has been deleted");

            verify(seatTypeRepository).findByIdAndDeletedFalse(seatTypeId);
            verify(userRepository, never()).findByUsername(anyString());
            verify(seatTypeRepository, never()).save(any(SeatTypeEntity.class));
            verify(bookingRepository, never()).save(any(BookingEntity.class));
        }

        @Test
        @DisplayName("Should throw ValidationException when seat type is already booked")
        void shouldThrowValidationExceptionWhenSeatTypeAlreadyBooked() {
            Long seatTypeId = 1L;
            var username = "testuser";
            var bookedSeatType = SeatTypeEntity.defaultBuilder()
                    .seatTypeCode("A1")
                    .buildDefault();
            
            ReflectionTestUtils.setField(bookedSeatType, "id", 1L);
            ReflectionTestUtils.setField(bookedSeatType, "seatTypeName", "Premium Seat");
            ReflectionTestUtils.setField(bookedSeatType, "workerMemo", "Test memo");
            ReflectionTestUtils.setField(bookedSeatType, "deleted", false);
            ReflectionTestUtils.setField(bookedSeatType, "isBooked", true);
            ReflectionTestUtils.setField(bookedSeatType, "version", 1);

            when(authentication.getName()).thenReturn(username);
            when(seatTypeRepository.findByIdAndDeletedFalse(seatTypeId))
                    .thenReturn(Optional.of(bookedSeatType));

            assertThatThrownBy(() -> bookingService.bookSeatType(seatTypeId, authentication))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("This seat type is already booked");

            verify(seatTypeRepository).findByIdAndDeletedFalse(seatTypeId);
            verify(userRepository, never()).findByUsername(anyString());
            verify(seatTypeRepository, never()).save(any(SeatTypeEntity.class));
            verify(bookingRepository, never()).save(any(BookingEntity.class));
        }

        @Test
        @DisplayName("Should throw NotFoundException when user not found")
        void shouldThrowNotFoundExceptionWhenUserNotFound() {
            Long seatTypeId = 1L;
            var username = "nonexistentuser";

            when(authentication.getName()).thenReturn(username);
            when(seatTypeRepository.findByIdAndDeletedFalse(seatTypeId))
                    .thenReturn(Optional.of(seatTypeEntity));
            when(userRepository.findByUsername(username))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.bookSeatType(seatTypeId, authentication))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("User not found");

            verify(seatTypeRepository).findByIdAndDeletedFalse(seatTypeId);
            verify(userRepository).findByUsername(username);
            verify(seatTypeRepository, never()).save(any(SeatTypeEntity.class));
            verify(bookingRepository, never()).save(any(BookingEntity.class));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Additional Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null authentication gracefully")
        void shouldHandleNullAuthenticationGracefully() {
            Long seatTypeId = 1L;

            assertThatThrownBy(() -> bookingService.bookSeatType(seatTypeId, null))
                    .isInstanceOf(NullPointerException.class);

            verifyNoInteractions(seatTypeRepository);
            verifyNoInteractions(userRepository);
            verifyNoInteractions(bookingRepository);
        }

        @Test
        @DisplayName("Should create booking with correct notes")
        void shouldCreateBookingWithCorrectNotes() {
            Long seatTypeId = 1L;
            var username = "testuser";

            when(authentication.getName()).thenReturn(username);
            when(seatTypeRepository.findByIdAndDeletedFalse(seatTypeId))
                    .thenReturn(Optional.of(seatTypeEntity));
            when(userRepository.findByUsername(username))
                    .thenReturn(Optional.of(userEntity));
            when(seatTypeRepository.save(any(SeatTypeEntity.class)))
                    .thenReturn(seatTypeEntity);

            bookingService.bookSeatType(seatTypeId, authentication);

            verify(bookingRepository).save(argThat(booking -> 
                booking.getNotes().equals("Seat booked via booking page") &&
                booking.getUser().equals(userEntity) &&
                booking.getSeatType().equals(seatTypeEntity)
            ));
        }
    }
}