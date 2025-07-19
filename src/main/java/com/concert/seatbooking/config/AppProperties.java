package com.concert.seatbooking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private UserConfig admin = new UserConfig();
    private UserConfig staff = new UserConfig();
    private UserConfig customer = new UserConfig();

    @Data
    public static class UserConfig {
        private String username;
        private String password;
    }
}