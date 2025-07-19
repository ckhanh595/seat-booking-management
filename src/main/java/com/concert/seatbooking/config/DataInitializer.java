package com.concert.seatbooking.config;

import com.concert.seatbooking.entity.UserEntity;
import com.concert.seatbooking.model.enums.UserRole;
import com.concert.seatbooking.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    @PostConstruct
    @Transactional
    public void initializeData() {
        log.info("Starting data initialization for users...");
        
        createUserIfNotExists(
            appProperties.getAdmin().getUsername(),
            appProperties.getAdmin().getPassword(),
            "System Administrator",
            UserRole.SUPER_ADMIN
        );
        
        createUserIfNotExists(
            appProperties.getStaff().getUsername(),
            appProperties.getStaff().getPassword(),
            "Staff Member",
            UserRole.STAFF
        );
        
        createUserIfNotExists(
            appProperties.getCustomer().getUsername(),
            appProperties.getCustomer().getPassword(),
            "Customer User",
            UserRole.CUSTOMER
        );
        
        log.info("Data initialization completed successfully.");
    }
    
    private void createUserIfNotExists(String username, String password, String fullName, UserRole role) {
        if (userRepository.findByUsername(username).isEmpty()) {
            String encodedPassword = passwordEncoder.encode(password);
            
            UserEntity user = UserEntity.builder()
                .username(username)
                .password(encodedPassword)
                .email(username)
                .fullName(fullName)
                .role(role)
                .build();
                
            userRepository.save(user);
            log.info("Created user: {} with role: {}", username, role);
        } else {
            log.info("User already exists: {}", username);
        }
    }


}
