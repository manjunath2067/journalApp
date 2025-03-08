package com.learning.journalApp.controller;

import java.util.List;

import com.learning.journalApp.cache.AppCache;
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

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppCache appCache;

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAll();
        if (users != null && !users.isEmpty()) {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create-admin-user")
    public void addAdmin(@RequestBody User user) {
        userService.saveAdmin(user);
    }


    /**
     * Endpoint to clear and reinitialize the application cache.
     * This method is useful for refreshing the cache with the latest
     * configuration values from the database without restarting the application.
     */
    @GetMapping("/clear-cache")
    public void clearCache() {
        appCache.init();
    }

}
