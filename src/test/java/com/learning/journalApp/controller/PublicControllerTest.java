package com.learning.journalApp.controller;

import com.learning.journalApp.entity.User;
import com.learning.journalApp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PublicControllerTest {

    @InjectMocks
    private PublicController publicController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        // Arrange
        User user = new User();
        user.setUserName("testuser");
        user.setPassword("password");

        when(userService.saveUser(any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<User> response = publicController.createUser(user);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }
}
