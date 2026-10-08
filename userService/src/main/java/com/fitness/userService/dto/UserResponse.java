package com.fitness.userService.dto;

import com.fitness.userService.model.UserRole;
import lombok.Data;


import java.time.LocalDate;
@Data
public class UserResponse {
    private String id;
    private String keycloakId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UserRole role=UserRole.USER;
    private LocalDate createdAt;
    private LocalDate updatedAt;

}
