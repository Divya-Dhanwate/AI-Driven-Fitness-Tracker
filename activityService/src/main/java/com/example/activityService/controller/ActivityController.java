package com.example.activityService.controller;

import com.example.activityService.dto.ActivityRequest;
import com.example.activityService.dto.ActivityResponse;
import com.example.activityService.model.Activity;
import com.example.activityService.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {

    private  final ActivityService activityService;
    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request, @RequestHeader("X-User-ID") String userId) {
        if(userId!=null){
            request.setUserId(userId);
        }
        return ResponseEntity.ok(activityService.trackActivity(request));
    }
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }
   /* @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivity(@RequestHeader("X-User-ID") String userId){
        return ResponseEntity.ok(activityService.getUserActivity(userId));
    }*/
   @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityResponse>> getUserActivity(@PathVariable String userId){
       System.out.println("getUserActivity method calls and it retrives {userId} : user from backend");
        return ResponseEntity.ok(activityService.getUserActivity(userId));
    }
    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivityById(@PathVariable String activityId)
    {
        return ResponseEntity.ok(activityService.getActivityById(activityId));
    }

    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivityById(@PathVariable String activityId, @RequestHeader("X-User-ID") String userId){
       boolean isDeleted=activityService.deleteActivityById(activityId,userId);
       if(isDeleted==true){
           return ResponseEntity.noContent().build();

       }

       return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
