package com.fitness.aiservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.Map;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Activity {
    private String id;
    private String userId;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private String type;
    private Map<String , Object> additionalMetrics;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
