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

import static com.concert.seatbooking.constant.AppConstants.PAGE_TITLE;
import static com.concert.seatbooking.constant.AppConstants.SEAT_TYPE;
import static com.concert.seatbooking.constant.AppConstants.SEAT_TYPES;
import static com.concert.seatbooking.constant.AppConstants.TOTAL_COUNT;
import static com.concert.seatbooking.constant.AppConstants.UPDATE_SEAT_TYPE_REQUEST;
import static com.concert.seatbooking.constant.CommonMessage.ERROR_MESSAGE;
import static com.concert.seatbooking.constant.CommonMessage.SEAT_TYPE_CREATED_SUCCESS;
import static com.concert.seatbooking.constant.CommonMessage.SEAT_TYPE_CREATE_ERROR;
import static com.concert.seatbooking.constant.CommonMessage.SEAT_TYPE_DELETED_SUCCESS;
import static com.concert.seatbooking.constant.CommonMessage.SEAT_TYPE_DELETE_ERROR;
import static com.concert.seatbooking.constant.CommonMessage.SEAT_TYPE_DUPLICATED_SUCCESS;
import static com.concert.seatbooking.constant.CommonMessage.SEAT_TYPE_DUPLICATE_ERROR;
import static com.concert.seatbooking.constant.CommonMessage.SEAT_TYPE_UPDATED_SUCCESS;
import static com.concert.seatbooking.constant.CommonMessage.SEAT_TYPE_UPDATE_ERROR;
import static com.concert.seatbooking.constant.CommonMessage.SUCCESS_MESSAGE;
import static com.concert.seatbooking.constant.PageConstants.CREATE_NEW_SEAT_TYPE;
import static com.concert.seatbooking.constant.PageConstants.EDIT_SEAT_TYPE;
import static com.concert.seatbooking.constant.PageConstants.SEAT_TYPES_MANAGEMENT;
import static com.concert.seatbooking.constant.ViewConstants.REDIRECT_SEAT_TYPES;
import static com.concert.seatbooking.constant.ViewConstants.REDIRECT_SEAT_TYPES_CREATE;
import static com.concert.seatbooking.constant.ViewConstants.SEAT_TYPES_CREATE;
import static com.concert.seatbooking.constant.ViewConstants.SEAT_TYPES_EDIT;
import static com.concert.seatbooking.constant.ViewConstants.SEAT_TYPES_LIST;
import static com.concert.seatbooking.mapper.SeatTypeMapper.toUpdateRequest;

@Controller
@RequestMapping("/seat-types")
@RequiredArgsConstructor
@Slf4j
public class SeatTypeController {

    private final SeatTypeService seatTypeService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute(PAGE_TITLE, CREATE_NEW_SEAT_TYPE);
        return SEAT_TYPES_CREATE;
    }

    @PostMapping("/create")
    public String createSeatType(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            var createdSeatType = seatTypeService.createSeatType(authentication);

            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE,
                    String.format(SEAT_TYPE_CREATED_SUCCESS,
                            createdSeatType.getSeatTypeName(), createdSeatType.getSeatTypeCode()));

            return REDIRECT_SEAT_TYPES;

        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
            return REDIRECT_SEAT_TYPES_CREATE;
        } catch (Exception e) {
            log.error("Error creating seat type", e);
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, SEAT_TYPE_CREATE_ERROR);
            return "redirect:/seat-types/create";
        }
    }

    @GetMapping
    public String listSeatTypes(Model model) {
        var seatTypes = seatTypeService.getAllSeatTypes();
        var totalCount = seatTypeService.getTotalSeatTypeCount();

        model.addAttribute(PAGE_TITLE, SEAT_TYPES_MANAGEMENT);
        model.addAttribute(SEAT_TYPES, seatTypes);
        model.addAttribute(TOTAL_COUNT, totalCount);

        return SEAT_TYPES_LIST;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            var seatType = seatTypeService.getSeatTypeById(id);
            var updateRequest = toUpdateRequest(seatType);

            model.addAttribute(PAGE_TITLE, EDIT_SEAT_TYPE);
            model.addAttribute(SEAT_TYPE, seatType);
            model.addAttribute(UPDATE_SEAT_TYPE_REQUEST, updateRequest);

            return SEAT_TYPES_EDIT;
        } catch (ValidationException e) {
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
            return REDIRECT_SEAT_TYPES;
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
                model.addAttribute(PAGE_TITLE, EDIT_SEAT_TYPE);
                model.addAttribute(SEAT_TYPE, seatType);

                return SEAT_TYPES_EDIT;
            }

            var updatedSeatType = seatTypeService.updateSeatType(id, request, authentication);

            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE,
                    String.format(SEAT_TYPE_UPDATED_SUCCESS,
                            updatedSeatType.getSeatTypeName()));

            return REDIRECT_SEAT_TYPES;

        } catch (ValidationException | NotFoundException e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
            return REDIRECT_SEAT_TYPES;
        } catch (Exception e) {
            log.error("Error updating seat type {}", id, e);
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, SEAT_TYPE_UPDATE_ERROR);
            return REDIRECT_SEAT_TYPES;
        }
    }

    @PostMapping("/{id}/duplicate")
    public String duplicateSeatType(@PathVariable Long id,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            var duplicatedSeatType = seatTypeService.duplicateSeatType(id, authentication);

            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE,
                    String.format(SEAT_TYPE_DUPLICATED_SUCCESS,
                            duplicatedSeatType.getSeatTypeName(), duplicatedSeatType.getSeatTypeCode()));

            return REDIRECT_SEAT_TYPES;

        } catch (ValidationException | NotFoundException e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
            return REDIRECT_SEAT_TYPES;
        } catch (Exception e) {
            log.error("Error duplicating seat type {}", id, e);
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, SEAT_TYPE_DUPLICATE_ERROR);
            return REDIRECT_SEAT_TYPES;
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteSeatType(@PathVariable Long id,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            seatTypeService.deleteSeatType(id, authentication);

            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, SEAT_TYPE_DELETED_SUCCESS);

            return REDIRECT_SEAT_TYPES;

        } catch (ValidationException | NotFoundException e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
            return REDIRECT_SEAT_TYPES;
        } catch (Exception e) {
            log.error("Error deleting seat type {}", id, e);
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, SEAT_TYPE_DELETE_ERROR);
            return REDIRECT_SEAT_TYPES;
        }
    }
}