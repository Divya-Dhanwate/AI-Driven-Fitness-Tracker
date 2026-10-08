package com.fitness.userService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
            public String home(){
        System.out.println("Home page is running for the UserService");
return "Home page is running";
    }
}
