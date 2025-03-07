package com.learning.journalApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.journalApp.entity.User;
import com.learning.journalApp.service.UserService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/create-user")
    @RateLimiter(name = "createUserRateLimiter", fallbackMethod = "createUserFallback")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        userService.saveNewUser(user);
        log.info("User created successfully: {}", user.getUserName());
        return ResponseEntity.ok("User created successfully");
    }

    // Fallback method to handle rate limit exceeded (RequestNotPermitted exception)
    public ResponseEntity<String> createUserFallback(
          User user,
          RequestNotPermitted ex
    )
    {
        log.warn("Rate limit exceeded for createUser endpoint for user: {}", user.getUserName());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
              .body("Too many requests. Please try again later.");
    }
}

