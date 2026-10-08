package com.example.activityService.service;

import com.example.activityService.dto.ActivityRequest;
import com.example.activityService.dto.ActivityResponse;
import com.example.activityService.model.Activity;
import com.example.activityService.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name:fitness_direct_exchange}")
    private String exchange;


    @Value("${rabbitmq.routing.key:activity-tracking}")
    private String routingKey;

    public  ActivityResponse trackActivity(ActivityRequest request) {
        boolean isValidUser = userValidationService.validateUser(request.getUserId());

        if (!isValidUser) {
            throw new RuntimeException("Invalid user " + request.getUserId());
        }

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getActivityType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())

                .startTime(request.getStartTime() != null ? request.getStartTime() : LocalDateTime.now())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity = activityRepository.save(activity);
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        } catch (Exception e) {
            log.error("Failed to publish activity to RabbitMQ for user: {}", request.getUserId(), e);
            return mapToResponse(savedActivity);
        }
        return mapToResponse(savedActivity);

    }
        private ActivityResponse mapToResponse(Activity activity) {
ActivityResponse response= new ActivityResponse();
response.setId(activity.getId());
response.setUserId(activity.getUserId());
response.setType(activity.getType());
response.setDuration(activity.getDuration());
response.setCaloriesBurned(activity.getCaloriesBurned());
response.setStartTime(activity.getStartTime());
response.setAdditionalMetrics(activity.getAdditionalMetrics());
response.setCreatedAt(activity.getCreatedAt());
response.setUpdatedAt(activity.getUpdatedAt());
return response;
    }

    public  List<ActivityResponse> getUserActivity(String userId) {
List<Activity> activities=activityRepository.findByUserId(userId);
return activities.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
    }

    public  ActivityResponse getActivityById(String activityId) {
        return activityRepository.findById(activityId)
                .map(this::mapToResponse)
                .orElseThrow(()-> new RuntimeException("Activity not found"+activityId));
    }

    public List<Activity> getAllActivities() {
        List<Activity> activities=activityRepository.findAll();
        return activities;
    }

    public boolean deleteActivityById(String activityId, String userId) {
        Optional<Activity> activityOpt=activityRepository.findById(activityId);
        if (activityOpt.isPresent()) {
            Activity activity=activityOpt.get();

            if(activity.getUserId().equals(userId)) {
                activityRepository.deleteById(activityId);
                log.warn("ACTIVITY DELETED SUCCESFULLY");
                return true;
            }
        }
        return false;
    }
}
