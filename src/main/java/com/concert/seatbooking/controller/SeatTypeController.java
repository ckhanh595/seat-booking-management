package com.concert.seatbooking.controller;

import com.concert.seatbooking.exception.NotFoundException;
import com.concert.seatbooking.exception.ValidationException;
import com.concert.seatbooking.model.seattype.UpdateSeatTypeRequest;
import com.concert.seatbooking.service.SeatTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.concert.seatbooking.mapper.SeatTypeMapper.toUpdateRequest;

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
        var seatTypes = seatTypeService.getAllSeatTypes();
        var totalCount = seatTypeService.getTotalSeatTypeCount();

        model.addAttribute("pageTitle", "Seat Types Management");
        model.addAttribute("seatTypes", seatTypes);
        model.addAttribute("totalCount", totalCount);

        return "seat-types/list";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            var seatType = seatTypeService.getSeatTypeById(id);
            var updateRequest = toUpdateRequest(seatType);

            model.addAttribute("pageTitle", "Edit Seat Type");
            model.addAttribute("seatType", seatType);
            model.addAttribute("updateSeatTypeRequest", updateRequest);

            return "seat-types/edit";
        } catch (ValidationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/seat-types";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateSeatType(@PathVariable Long id,
                                 @Valid @ModelAttribute UpdateSeatTypeRequest request,
                                 BindingResult bindingResult,
                                 Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                log.warn("Validation error occurred while updating seat type {}", id);
                var seatType = seatTypeService.getSeatTypeById(id);
                model.addAttribute("pageTitle", "Edit Seat Type");
                model.addAttribute("seatType", seatType);

                return "seat-types/edit";
            }

            var updatedSeatType = seatTypeService.updateSeatType(id, request, authentication);

            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("Seat type '%s' updated successfully",
                            updatedSeatType.getSeatTypeName()));

            return "redirect:/seat-types";

        } catch (ValidationException | NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/seat-types";
        } catch (Exception e) {
            log.error("Error updating seat type {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An error occurred while updating the seat type. Please try again.");
            return "redirect:/seat-types";
        }
    }
}