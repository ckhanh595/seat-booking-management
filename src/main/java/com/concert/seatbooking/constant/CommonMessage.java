package com.concert.seatbooking.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class CommonMessage {

    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String LOGOUT_MESSAGE = "logoutMessage";

    public static final String SEAT_BOOKED_SUCCESS = "Seat type booked successfully! Thank you for your booking.";
    public static final String BOOKING_ERROR = "An error occurred while booking the seat type. Please try again.";

    public static final String SEAT_TYPE_CREATED_SUCCESS = "Seat type '%s' created successfully with code %s";
    public static final String SEAT_TYPE_CREATE_ERROR = "An error occurred while creating the seat type. Please try again.";

    public static final String SEAT_TYPE_UPDATED_SUCCESS = "Seat type '%s' updated successfully";
    public static final String SEAT_TYPE_UPDATE_ERROR = "An error occurred while updating the seat type. Please try again.";

    public static final String SEAT_TYPE_DUPLICATED_SUCCESS = "Seat type duplicated successfully. New seat type '%s' created with code %s";
    public static final String SEAT_TYPE_DUPLICATE_ERROR = "An error occurred while duplicating the seat type. Please try again.";

    public static final String SEAT_TYPE_DELETED_SUCCESS = "Seat type deleted successfully";
    public static final String SEAT_TYPE_DELETE_ERROR = "An error occurred while deleting the seat type. Please try again.";

    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password!";
    public static final String LOGOUT_SUCCESS = "You have been logged out successfully!";
}