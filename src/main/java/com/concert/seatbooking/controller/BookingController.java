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

@Controller
@RequestMapping("/book-seats")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public String showBookingPage(Model model) {
        var availableSeatTypes = bookingService.getAvailableSeatTypes();
        
        model.addAttribute("pageTitle", "Book Seats");
        model.addAttribute("availableSeatTypes", availableSeatTypes);
        
        return "booking/list";
    }

    @PostMapping("/book/{seatTypeId}")
    public String bookSeatType(@PathVariable Long seatTypeId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            bookingService.bookSeatType(seatTypeId, authentication);
            
            redirectAttributes.addFlashAttribute("successMessage",
                    "Seat type booked successfully! Thank you for your booking.");
            
            return "redirect:/book-seats";
            
        } catch (ValidationException | NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/book-seats";
        } catch (Exception e) {
            log.error("Error booking seat type {} for user: {}", seatTypeId, authentication.getName(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An error occurred while booking the seat type. Please try again.");
            return "redirect:/book-seats";
        }
    }
}