package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAiService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder builder;

    public Recommendation generateRecommendation(Activity activity) {
        try {
            String prompt = createPromptForActivity(activity);
            String aiResponse = geminiService.getAnswer(prompt);

            log.info("RESPONSE FROM AI: {}", aiResponse);

            Recommendation recommendation = parseAiTextResponse(aiResponse, activity);
            if (recommendation == null) {
                log.warn("Could not parse AI response. Using default recommendation.");
                return createDefaultRecommendation(activity);
            }
            return recommendation;
        } catch (Exception e) {
            log.error("Error generating recommendation", e);
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        String safeType = (activity != null && activity.getType() != null) ? activity.getType() : "Workout";
        String safeActivityId = (activity != null) ? activity.getId() : "Unknown";
        String safeUserId = (activity != null) ? activity.getUserId() : "Unknown";

        return Recommendation.builder()
                .activityId(safeActivityId)
                .userId(safeUserId)
                .activityType(safeType)
                .createdAt(activity != null && activity.getCreatedAt() != null ? activity.getCreatedAt() : LocalDateTime.now())
                .recommendations("Your workout was successfully recorded. Keep maintaining consistency and gradually improve your performance.")
                .improvement(Collections.singletonList("Continue exercising regularly and focus on gradual improvement."))
                .suggestions(Collections.singletonList("Maintain a consistent workout routine and stay properly hydrated."))
                .safety(Collections.singletonList("Follow proper warm-up and cool-down procedures and listen to your body."))
                .build();
    }

    private Recommendation parseAiTextResponse(String aiResponse, Activity activity) {
        try {
            JsonNode rootNode = objectMapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text");

            if (textNode.isMissingNode()) {
                log.warn("Could not find text in Gemini response");
                return null;
            }

            String rawText = textNode.asText().trim();
            String jsonContent = rawText;
            if (jsonContent.startsWith("```json")) {
                jsonContent = jsonContent.substring(7);
            } else if (jsonContent.startsWith("```")) {
                jsonContent = jsonContent.substring(3);
            }
            if (jsonContent.endsWith("```")) {
                jsonContent = jsonContent.substring(0, jsonContent.length() - 3);
            }
            jsonContent = jsonContent.trim();

            log.info("Parsed JSON Content: {}", jsonContent);
            JsonNode analysisJson = objectMapper.readTree(jsonContent);

            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:\n");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:\n");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:\n");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories Burned:\n");

            List<String> improvements = extractImprovement(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestion"));
            List<String> safety = extractSafetyGuideline(analysisJson.path("safety"));

            String dynamicType = (activity != null && activity.getType() != null) ? activity.getType() : "Workout";

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(dynamicType)
                    .recommendations(fullAnalysis.toString())
                    .improvement(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(activity.getCreatedAt() != null ? activity.getCreatedAt() : LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error parsing AI text response", e);
            return null;
        }
    }

    private String createPromptForActivity(Activity activity) {
        String type = (activity != null && activity.getType() != null) ? activity.getType().toLowerCase() : "workout";

        return String.format("""
        You are an expert fitness AI coach.
        Analyze the user's workout activity in detail.

        Activity Type: %s
        Activity ID: %s
        User ID: %s
        Duration: %d minutes
        Calories Burned: %d kcal
        Start Time: %s
        Additional Metrics: %s

        IMPORTANT:
        The user supports ONLY these activity types:
        - running
        - cycling
        - swimming

        Analyze the activity according to its type.
        Respond with ONLY valid JSON inside markdown blocks:
        {
          "analysis": {
            "overall": "detailed overall analysis",
            "pace": "activity-specific pace analysis",
            "heartRate": "intensity analysis based only on available data",
            "caloriesBurned": "detailed calorie analysis"
          },
          "improvements": [
            { "area": "specific area", "recommendation": "specific actionable recommendation" }
          ],
          "suggestion": [
            { "workout": "specific next workout", "description": "detailed explanation" }
          ],
          "safety": [
            "specific safety recommendation"
          ]
        }
        """,
                type,
                activity.getId(),
                activity.getUserId(),
                activity.getDuration() != null ? activity.getDuration() : 0,
                activity.getCaloriesBurned() != null ? activity.getCaloriesBurned() : 0,
                activity.getStartTime() != null ? activity.getStartTime().toString() : "Unknown",
                activity.getAdditionalMetrics() != null ? activity.getAdditionalMetrics().toString() : "None provided"
        );
    }

    private List<String> extractImprovement(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if (improvementsNode.isArray()) {
            improvementsNode.forEach(improvement -> {
                if (improvement.isObject()) {
                    String area = improvement.path("area").asText("");
                    String detail = improvement.path("recommendation").asText("");
                    if (!area.isEmpty() || !detail.isEmpty()) {
                        improvements.add(area + " : " + detail);
                    }
                } else {
                    String text = improvement.asText("");
                    if (!text.isEmpty()) improvements.add(text);
                }
            });
        }
        return improvements.isEmpty() ? Collections.singletonList("Focus on gradual physical performance updates.") : improvements;
    }

    private List<String> extractSuggestions(JsonNode suggestionNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionNode.isArray()) {
            suggestionNode.forEach(suggestion -> {
                if (suggestion.isObject()) {
                    String workout = suggestion.path("workout").asText("");
                    String description = suggestion.path("description").asText("");
                    if (!workout.isEmpty() || !description.isEmpty()) {
                        suggestions.add(workout + " : " + description);
                    }
                } else {
                    String text = suggestion.asText("");
                    if (!text.isEmpty()) suggestions.add(text);
                }
            });
        }
        return suggestions.isEmpty() ? Collections.singletonList("Maintain a consistent structured workout routine.") : suggestions;
    }

    private List<String> extractSafetyGuideline(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> {
                String text = item.asText("");
                if (!text.isEmpty()) safety.add(text);
            });
        }
        return safety.isEmpty() ? Collections.singletonList("Follow standard warmup routines.") : safety;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if (analysisNode != null && !analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix).append(analysisNode.path(key).asText()).append("\n\n");
        }
    }
}
