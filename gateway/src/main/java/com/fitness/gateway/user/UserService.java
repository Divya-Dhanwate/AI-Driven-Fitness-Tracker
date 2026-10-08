package com.fitness.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {
        log.info("Calling user validation API for userId : {}", userId);

        return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(new RuntimeException("User not found: " + userId));
                    } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new RuntimeException("Invalid Request: " + userId));
                    }

                    return Mono.error(new RuntimeException("Unexpected Error: " + e.getMessage()));
                });
    }

    public Mono<UserResponse> registerRequest(RegisterRequest request) {

        log.info(
                "Calling user registration API for email: {}",
                request.getEmail()
        );

        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {

                    log.error(
                            "USER SERVICE STATUS: {}",
                            e.getStatusCode()
                    );

                    log.error(
                            "USER SERVICE RESPONSE: {}",
                            e.getResponseBodyAsString()
                    );

                    return Mono.error(
                            new RuntimeException(
                                    "User Service returned "
                                            + e.getStatusCode()
                                            + ": "
                                            + e.getResponseBodyAsString()
                            )
                    );
                });
    }
}
