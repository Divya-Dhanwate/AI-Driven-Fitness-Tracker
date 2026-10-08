package com.fitness.gateway;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class KeycloakUserSyncFilter implements WebFilter {
    private final UserService userService;
    public KeycloakUserSyncFilter(UserService userService) {
        this.userService = userService;
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (token==null || token.isBlank()){
            return chain.filter(exchange);
        }
        RegisterRequest registerRequest = getUserDetails(token);
        if(userId==null || userId.isBlank()){
            userId=registerRequest.getKeycloakId();
        }
        if (registerRequest.getKeycloakId() != null && token != null) {
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if (!exist) {
                            if (registerRequest != null) {
                                return userService.registerRequest(registerRequest)
                                        .then(Mono.empty());

                            }
                            return Mono.empty();
                        } else {
                            log.info("userId " + finalUserId + " token " + token);
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(() -> {
                        ServerHttpRequest mutateRequest = exchange.getRequest().mutate()
                                .header("X-User-ID", finalUserId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutateRequest).build());
                    }));
        }

        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {
        try{
          String tokenWithoutBearer=token.replace("Bearer ", "").trim();
          SignedJWT signedJwt=SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims=signedJwt.getJWTClaimsSet();

            RegisterRequest registerRequest=new RegisterRequest();
            registerRequest.setEmail(claims.getStringClaim("email"));
            registerRequest.setKeycloakId(claims.getStringClaim("sub"));
            registerRequest.setPassword("dummy@123");
            registerRequest.setFirstName(claims.getStringClaim("given_name"));
            registerRequest.setLastName(claims.getStringClaim("family_name"));
            return registerRequest;

        }catch (Exception e) {
            log.error("Failed to extract user details from Keycloak token", e);
            return new RegisterRequest();
        }
    }

}
