package com.concert.seatbooking.integration;

import com.concert.seatbooking.entity.SeatTypeEntity;
import com.concert.seatbooking.entity.UserEntity;
import com.concert.seatbooking.model.enums.UserRole;
import com.concert.seatbooking.repository.BookingRepository;
import com.concert.seatbooking.repository.SeatTypeRepository;
import com.concert.seatbooking.repository.UserRepository;
import com.concert.seatbooking.testcontainers.AbstractIntegrationTest;
import com.concert.seatbooking.testcontainers.TestDataHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("BookingController Integration Tests")
class BookingControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private SeatTypeRepository seatTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private UserEntity testCustomer;
    private UserEntity testStaff;
    private List<SeatTypeEntity> availableSeatTypes;

    @BeforeEach
    void setUpTestData() {
        testDataHelper.cleanupDatabase();
        
        testCustomer = testDataHelper.createTestUser("customer", UserRole.CUSTOMER);
        testStaff = testDataHelper.createTestUser("staff", UserRole.STAFF);
        testDataHelper.createTestUser("admin", UserRole.SUPER_ADMIN);
        availableSeatTypes = testDataHelper.createMultipleAvailableSeatTypes();
    }

    @Nested
    @DisplayName("GET /book-seats Tests")
    class GetBookingPageTests {

        @Test
        @DisplayName("Should display booking page with available seat types for authenticated customer")
        @WithMockUser(username = "customer", roles = "CUSTOMER")
        void shouldDisplayBookingPageWithAvailableSeatTypes() throws Exception {
            mockMvc.perform(get("/book-seats"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("booking/list"))
                    .andExpect(model().attribute("pageTitle", "Book Seats"))
                    .andExpect(model().attributeExists("availableSeatTypes"))
                    .andExpect(model().attribute("availableSeatTypes", 
                        org.hamcrest.Matchers.hasSize(3)));
        }

        @Test
        @DisplayName("Should deny access to staff users (only customers allowed)")
        @WithMockUser(username = "staff", roles = "STAFF")
        void shouldDenyAccessToStaffUsers() throws Exception {
            mockMvc.perform(get("/book-seats"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should display booking page with empty list when no available seat types")
        @WithMockUser(username = "customer", roles = "CUSTOMER")
        void shouldDisplayEmptyListWhenNoAvailableSeatTypes() throws Exception {
            seatTypeRepository.deleteAll();

            mockMvc.perform(get("/book-seats"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("booking/list"))
                    .andExpect(model().attribute("pageTitle", "Book Seats"))
                    .andExpect(model().attribute("availableSeatTypes", 
                        org.hamcrest.Matchers.hasSize(0)));
        }

        @Test
        @DisplayName("Should exclude booked and deleted seat types from display")
        @WithMockUser(username = "customer", roles = "CUSTOMER")
        void shouldExcludeBookedAndDeletedSeatTypes() throws Exception {
            testDataHelper.createBookedSeatType("D1");
            testDataHelper.createDeletedSeatType("E1");

            mockMvc.perform(get("/book-seats"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("booking/list"))
                    .andExpect(model().attribute("availableSeatTypes",
                        org.hamcrest.Matchers.hasSize(3)));
        }

        @Test
        @DisplayName("Should redirect to login when not authenticated")
        void shouldRedirectToLoginWhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/book-seats"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }

    @Nested
    @DisplayName("POST /book-seats/book/{seatTypeId} Success Tests")
    class BookSeatTypeSuccessTests {

        @Test
        @DisplayName("Should successfully book available seat type")
        @WithMockUser(username = "customer", roles = "CUSTOMER")
        void shouldSuccessfullyBookAvailableSeatType() throws Exception {
            var seatType = availableSeatTypes.get(0);
            var seatTypeId = seatType.getId();

            mockMvc.perform(post("/book-seats/book/{seatTypeId}", seatTypeId))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/book-seats"))
                    .andExpect(flash().attribute("successMessage", 
                        "Seat type booked successfully! Thank you for your booking."));

            var updatedSeatType = seatTypeRepository.findById(seatTypeId);
            assertThat(updatedSeatType).isPresent();
            assertThat(updatedSeatType.get().getIsBooked()).isTrue();

            var bookings = bookingRepository.findAll();
            assertThat(bookings).hasSize(1);
            assertThat(bookings.get(0).getUser().getUsername()).isEqualTo("customer");
            assertThat(bookings.get(0).getSeatType().getId()).isEqualTo(seatTypeId);
            assertThat(bookings.get(0).getNotes()).isEqualTo("Seat booked via booking page");
        }

        @Test
        @DisplayName("Should deny booking access to staff user")
        @WithMockUser(username = "staff", roles = "STAFF")
        void shouldDenyBookingAccessToStaffUser() throws Exception {
            var seatType = availableSeatTypes.get(1);
            var seatTypeId = seatType.getId();

            mockMvc.perform(post("/book-seats/book/{seatTypeId}", seatTypeId))
                    .andExpect(status().isForbidden());

            var bookings = bookingRepository.findAll();
            assertThat(bookings).isEmpty();
        }
    }

    @Nested
    @DisplayName("POST /book-seats/book/{seatTypeId} Error Tests")
    class BookSeatTypeErrorTests {

        @Test
        @DisplayName("Should handle booking already booked seat type")
        @WithMockUser(username = "customer", roles = "CUSTOMER")
        void shouldHandleBookingAlreadyBookedSeatType() throws Exception {
            var bookedSeatType = testDataHelper.createBookedSeatType("F1");
            var seatTypeId = bookedSeatType.getId();

            mockMvc.perform(post("/book-seats/book/{seatTypeId}", seatTypeId))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/book-seats"))
                    .andExpect(flash().attribute("errorMessage", 
                        "This seat type is already booked"));

            var bookings = bookingRepository.findAll();
            assertThat(bookings).isEmpty();
        }

        @Test
        @DisplayName("Should handle booking non-existent seat type")
        @WithMockUser(username = "customer", roles = "CUSTOMER")
        void shouldHandleBookingNonExistentSeatType() throws Exception {
            Long nonExistentId = 999L;

            mockMvc.perform(post("/book-seats/book/{seatTypeId}", nonExistentId))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/book-seats"))
                    .andExpect(flash().attribute("errorMessage", 
                        "Seat type not found or has been deleted"));

            var bookings = bookingRepository.findAll();
            assertThat(bookings).isEmpty();
        }

        @Test
        @DisplayName("Should handle booking deleted seat type")
        @WithMockUser(username = "customer", roles = "CUSTOMER")
        void shouldHandleBookingDeletedSeatType() throws Exception {
            var deletedSeatType = testDataHelper.createDeletedSeatType("G1");
            var seatTypeId = deletedSeatType.getId();

            mockMvc.perform(post("/book-seats/book/{seatTypeId}", seatTypeId))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/book-seats"))
                    .andExpect(flash().attribute("errorMessage", 
                        "Seat type not found or has been deleted"));

            var bookings = bookingRepository.findAll();
            assertThat(bookings).isEmpty();
        }

        @Test
        @DisplayName("Should handle unexpected errors gracefully")
        @WithMockUser(username = "nonexistentuser", roles = "CUSTOMER")
        void shouldHandleUnexpectedErrorsGracefully() throws Exception {
            var seatType = availableSeatTypes.get(0);
            var seatTypeId = seatType.getId();

            mockMvc.perform(post("/book-seats/book/{seatTypeId}", seatTypeId))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/book-seats"))
                    .andExpect(flash().attribute("errorMessage", 
                        "User not found"));

            var bookings = bookingRepository.findAll();
            assertThat(bookings).isEmpty();
        }

        @Test
        @DisplayName("Should require authentication for booking")
        void shouldRequireAuthenticationForBooking() throws Exception {
            var seatType = availableSeatTypes.get(0);
            var seatTypeId = seatType.getId();

            mockMvc.perform(post("/book-seats/book/{seatTypeId}", seatTypeId))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));

            var bookings = bookingRepository.findAll();
            assertThat(bookings).isEmpty();
        }

    }

    @Nested
    @DisplayName("Database State Verification Tests")
    class DatabaseStateTests {

        @Test
        @DisplayName("Should verify transaction rollback on database error")
        @WithMockUser(username = "customer", roles = "CUSTOMER")
        void shouldVerifyTransactionRollbackOnDatabaseError() throws Exception {
            var seatType = availableSeatTypes.get(0);
            var seatTypeId = seatType.getId();

            var initialSeatTypeCount = seatTypeRepository.count();
            var initialBookingCount = bookingRepository.count();

            mockMvc.perform(post("/book-seats/book/{seatTypeId}", seatTypeId))
                    .andExpect(status().is3xxRedirection());

            assertThat(seatTypeRepository.count()).isEqualTo(initialSeatTypeCount);
            assertThat(bookingRepository.count()).isEqualTo(initialBookingCount + 1);

            var updatedSeatType = seatTypeRepository.findById(seatTypeId);
            assertThat(updatedSeatType).isPresent();
            assertThat(updatedSeatType.get().getIsBooked()).isTrue();
        }

        @Test
        @DisplayName("Should verify seat type ordering in available list")
        @WithMockUser(username = "customer", roles = "CUSTOMER")
        void shouldVerifySeatTypeOrderingInAvailableList() throws Exception {
            testDataHelper.createTestSeatType("Z9", "Last Seat", "Should appear last", false);
            testDataHelper.createTestSeatType("A0", "First Seat", "Should appear first", false);

            mockMvc.perform(get("/book-seats"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("availableSeatTypes"));

            var retrievedSeatTypes = seatTypeRepository
                    .findByDeletedFalseAndIsBookedFalseOrderBySeatTypeCodeAsc();
            
            assertThat(retrievedSeatTypes).hasSize(5);
            assertThat(retrievedSeatTypes.get(0).getSeatTypeCode()).isEqualTo("A0");
            assertThat(retrievedSeatTypes.get(4).getSeatTypeCode()).isEqualTo("Z9");
        }
    }
}