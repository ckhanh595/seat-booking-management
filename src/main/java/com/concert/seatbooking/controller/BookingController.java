package com.concert.seatbooking.controller;

import com.concert.seatbooking.exception.NotFoundException;
import com.concert.seatbooking.exception.ValidationException;
import com.concert.seatbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.concert.seatbooking.constant.AppConstants.AVAILABLE_SEAT_TYPES;
import static com.concert.seatbooking.constant.AppConstants.PAGE_TITLE;
import static com.concert.seatbooking.constant.CommonMessage.BOOKING_ERROR;
import static com.concert.seatbooking.constant.CommonMessage.ERROR_MESSAGE;
import static com.concert.seatbooking.constant.CommonMessage.SEAT_BOOKED_SUCCESS;
import static com.concert.seatbooking.constant.CommonMessage.SUCCESS_MESSAGE;
import static com.concert.seatbooking.constant.PageConstants.BOOK_SEATS;
import static com.concert.seatbooking.constant.ViewConstants.BOOKING_LIST;
import static com.concert.seatbooking.constant.ViewConstants.REDIRECT_BOOK_SEATS;

@Controller
@RequestMapping("/book-seats")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public String showBookingPage(Model model) {
        var availableSeatTypes = bookingService.getAvailableSeatTypes();
        
        model.addAttribute(PAGE_TITLE, BOOK_SEATS);
        model.addAttribute(AVAILABLE_SEAT_TYPES, availableSeatTypes);
        
        return BOOKING_LIST;
    }

    @PostMapping("/book/{seatTypeId}")
    public String bookSeatType(@PathVariable Long seatTypeId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            bookingService.bookSeatType(seatTypeId, authentication);
            
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, SEAT_BOOKED_SUCCESS);
            
            return REDIRECT_BOOK_SEATS;
            
        } catch (ValidationException | NotFoundException e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
            return REDIRECT_BOOK_SEATS;
        } catch (Exception e) {
            log.error("Error booking seat type {} for user: {}", seatTypeId, authentication.getName(), e);
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, BOOKING_ERROR);
            return REDIRECT_BOOK_SEATS;
        }
    }
}