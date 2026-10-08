package com.fitness.userService.repository;

import com.fitness.userService.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {


    boolean existsByEmail(@NotBlank(message = "email is required")
                          @Email(message = "Invalid email formate") String email);

    Boolean existsByKeycloakId(String keycloakId);

    User findByEmail(String email);

    Optional<User> findByKeycloakId(String keycloakId);
}
