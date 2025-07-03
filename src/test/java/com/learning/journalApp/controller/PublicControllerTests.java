package com.learning.journalApp.controller;

import com.learning.journalApp.entity.User;
import com.learning.journalApp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PublicControllerTests {

    @InjectMocks
    private PublicController publicController;

    @Mock
    private UserService userService;

    @Test
    void testHealthCheck_returnsOkString() {
        // Act
        String response = publicController.healthCheck();

        // Assert
        assertEquals("OK", response);
    }

    @Test
    void testCreateUser_success_returnsOk() {
        // Arrange
        User testUser = new User();
        testUser.setUserName("newUser");
        testUser.setPassword("password");

        // userService.saveNewUser returns boolean, but controller doesn't use the return value directly for response
        when(userService.saveNewUser(any(User.class))).thenReturn(true);

        // Act
        ResponseEntity<String> response = publicController.createUser(testUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User created successfully", response.getBody());
        verify(userService, times(1)).saveNewUser(testUser);
    }

    @Test
    void testCreateUser_userServiceThrowsException_propagatesException() {
        // Arrange
        User testUser = new User();
        testUser.setUserName("newUser");
        testUser.setPassword("password");

        when(userService.saveNewUser(any(User.class))).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            publicController.createUser(testUser);
        });

        assertEquals("DB error", exception.getMessage());
        verify(userService, times(1)).saveNewUser(testUser);
    }

    @Test
    void testCreateUser_userServiceReturnsFalse_stillReturnsOk() {
        // Arrange
        User testUser = new User();
        testUser.setUserName("newUser");
        testUser.setPassword("password");

        // Simulate a scenario where saveNewUser might return false (e.g., user already exists, not handled by exception)
        // The current controller logic doesn't check this boolean return from saveNewUser, always assumes success if no exception.
        when(userService.saveNewUser(any(User.class))).thenReturn(false);

        // Act
        ResponseEntity<String> response = publicController.createUser(testUser);

        // Assert
        // This test highlights that the controller currently doesn't differentiate based on the boolean from saveNewUser.
        // It will still return OK unless an exception is thrown.
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User created successfully", response.getBody());
        verify(userService, times(1)).saveNewUser(testUser);
    }
}
