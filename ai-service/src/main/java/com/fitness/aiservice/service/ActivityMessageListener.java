package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class ActivityMessageListener {

    private final ActivityAiService aiService;
    private final RecommendationRepository recommendationRepository;
    private final ObjectMapper objectMapper;

    public ActivityMessageListener(
            ActivityAiService aiService,
            RecommendationRepository recommendationRepository,
            ObjectMapper objectMapper) {

        this.aiService = aiService;
        this.recommendationRepository = recommendationRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "activity.queue")
    public void proccessesActivity(
            Map<String, Object> activityMap) {

        try {
            String activityId =String.valueOf(activityMap.get("id"));
            log.info("=============================================");
            log.info("Received activity for processing: {}",activityId);
            log.info("=============================================");

            Activity activity =objectMapper.convertValue(activityMap,Activity.class);
            log.info("Activity converted successfully: {}",activity.getId());
            log.info("========== AI ACTIVITY DEBUG ==========");
            log.info("RabbitMQ activityMap = {}",activityMap);
            log.info("AI Activity type = {}",activity.getType());
            log.info("AI Activity additionalMetrics = {}",activity.getAdditionalMetrics());
            log.info("=======================================");
            Recommendation recommendation =aiService.generateRecommendation(activity);
            recommendationRepository.save(recommendation);
            log.info("Generated recommendation: {}",recommendation);
        } catch (Exception e) {
            log.error(
                    "Error processing activity message",
                    e
            );
        }
    }
}