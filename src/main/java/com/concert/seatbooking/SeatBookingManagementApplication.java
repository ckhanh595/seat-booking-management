package com.concert.seatbooking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main application class for Concert Seat Booking Management System.
 * <p>
 * This application provides CRUD operations for seat type patterns
 * and booking functionality for concerts.
 */
@Slf4j
@SpringBootApplication
@EnableConfigurationProperties
public class SeatBookingManagementApplication {

    public static void main(String[] args) {
        log.info("Starting Concert Seat Booking Management Application...");
        SpringApplication.run(SeatBookingManagementApplication.class, args);
        log.info("Concert Seat Booking Management Application started successfully!");
    }
}