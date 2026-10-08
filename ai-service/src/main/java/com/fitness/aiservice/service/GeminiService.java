package com.fitness.aiservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final WebClient webClient;
    private final String geminiApiKey;
    private final String primaryModel;
    private final String fallbackModel;

    public GeminiService(
            WebClient.Builder webClientBuilder,
            @Value("${gemini.api.uri:https://generativelanguage.googleapis.com}") String baseUri,
            @Value("${gemini.api.key}") String geminiApiKey,
            @Value("${gemini.api.model:gemini-flash-latest}") String primaryModel,
            @Value("${gemini.api.fallback-model:gemini-3.1-flash-lite}") String fallbackModel) {

        this.geminiApiKey = geminiApiKey;
        this.primaryModel = primaryModel;
        this.fallbackModel = fallbackModel;
        this.webClient = webClientBuilder
                .baseUrl(baseUri)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE)))
                .build();    }

    public static class GeminiApiException extends RuntimeException {
        private final int status;

        public GeminiApiException(int status, String body) {
            super("Gemini API Error: " + status + " - " + body);
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public boolean isRetryable() {
            return status == 503 || status == 429 || status == 500;
        }
    }

    public String getAnswer(String question) {
        try {
            return callModel(primaryModel, question);
        } catch (GeminiApiException primaryError) {
            log.warn("Primary model '{}' failed ({}). Trying fallback '{}'.",
                    primaryModel, primaryError.getStatus(), fallbackModel);
            try {
                return callModel(fallbackModel, question);
            } catch (GeminiApiException fallbackError) {
                log.error("Fallback model '{}' also failed: {}", fallbackModel, fallbackError.getMessage());
                throw fallbackError;
            }
        }
    }

    private String callModel(String model, String question) {
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", question)))
                )
        );

        return webClient.post()
                .uri("/v1beta/models/{model}:generateContent", model)
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", geminiApiKey)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        resp -> resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(errorBody -> Mono.error(
                                        new GeminiApiException(resp.statusCode().value(), errorBody)))
                )
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .maxBackoff(Duration.ofSeconds(10))
                        .filter(ex -> ex instanceof GeminiApiException g && g.isRetryable())
                        // surface the original exception instead of Reactor's RetryExhaustedException
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                .block();
    }

    public String getAiResponse(String prompt) {
        return getAnswer(prompt);
    }
}