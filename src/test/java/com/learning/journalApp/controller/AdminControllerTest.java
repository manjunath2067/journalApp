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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        User user1 = new User();
        user1.setUserName("user1");
        User user2 = new User();
        user2.setUserName("user2");
        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<?> response = adminController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    void testGetAllUsers_noUsersFound() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(null);

        // Act
        ResponseEntity<?> response = adminController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No users found", response.getBody());
    }

    @Test
    void testDeleteUserById() {
        // Arrange
        String userId = "testId";
        doNothing().when(userService).deleteUserById(userId);

        // Act
        ResponseEntity<?> response = adminController.deleteUserById(userId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void testDeleteUserById_exception() {
        // Arrange
        String userId = "testId";
        doThrow(new RuntimeException("User not found")).when(userService).deleteUserById(userId);

        // Act
        ResponseEntity<?> response = adminController.deleteUserById(userId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void testSaveEntry() {
        // Arrange
        User user = new User();
        user.setUserName("testUser");
        when(userService.saveUser(any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<User> response = adminController.saveEntry(new User());

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }
}
