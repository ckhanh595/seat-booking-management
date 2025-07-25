package com.concert.seatbooking.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error occurred", ex);
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
        return "error";
    }

    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(ValidationException ex, Model model, RedirectAttributes redirectAttributes) {
        log.warn("Business exception occurred: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/seat-types";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException ex, Model model, RedirectAttributes redirectAttributes) {
        log.warn("Not found exception occurred: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/seat-types";
    }
}