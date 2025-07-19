package com.concert.seatbooking.controller;

import com.concert.seatbooking.exception.ValidationException;
import com.concert.seatbooking.service.SeatTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/seat-types")
@RequiredArgsConstructor
@Slf4j
public class SeatTypeController {

    private final SeatTypeService seatTypeService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("pageTitle", "Create New Seat Type");
        return "seat-types/create";
    }

    @PostMapping("/create")
    public String createSeatType(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            var createdSeatType = seatTypeService.createSeatType(authentication);

            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("Seat type '%s' created successfully with code %s",
                            createdSeatType.getSeatTypeName(), createdSeatType.getSeatTypeCode()));

            return "redirect:/seat-types";

        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/seat-types/create";
        } catch (Exception e) {
            log.error("Error creating seat type", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An error occurred while creating the seat type. Please try again.");
            return "redirect:/seat-types/create";
        }
    }

    @GetMapping
    public String listSeatTypes(Model model) {
        model.addAttribute("pageTitle", "Seat Types Management");
        return "seat-types/list";
    }
}