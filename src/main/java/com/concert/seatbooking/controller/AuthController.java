package com.concert.seatbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.concert.seatbooking.constant.AppConstants.ERROR;
import static com.concert.seatbooking.constant.AppConstants.LOGOUT;
import static com.concert.seatbooking.constant.CommonMessage.ERROR_MESSAGE;
import static com.concert.seatbooking.constant.CommonMessage.INVALID_USERNAME_OR_PASSWORD;
import static com.concert.seatbooking.constant.CommonMessage.LOGOUT_MESSAGE;
import static com.concert.seatbooking.constant.CommonMessage.LOGOUT_SUCCESS;
import static com.concert.seatbooking.constant.ViewConstants.AUTH_LOGIN;
import static com.concert.seatbooking.constant.ViewConstants.DASHBOARD_INDEX;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(@RequestParam(value = ERROR, required = false) String error,
                       @RequestParam(value = LOGOUT, required = false) String logout,
                       Model model) {
        
        if (error != null) {
            model.addAttribute(ERROR_MESSAGE, INVALID_USERNAME_OR_PASSWORD);
        }
        
        if (logout != null) {
            model.addAttribute(LOGOUT_MESSAGE, LOGOUT_SUCCESS);
        }
        
        return AUTH_LOGIN;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return DASHBOARD_INDEX;
    }
}