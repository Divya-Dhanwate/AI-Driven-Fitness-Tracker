package com.example.activityService.dto;

import com.example.activityService.model.ActivityType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityRequest {

    private String userId;
@JsonProperty("type")
    private ActivityType activityType;
    private Integer duration;
    private Integer caloriesBurned;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss" )
    private LocalDateTime startTime;
    private Map<String , Object> additionalMetrics;

}
