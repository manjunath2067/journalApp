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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockAuthentication(String username) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
    }

    @Test
    void testGetAllUsers_success() {
        // Arrange
        User user1 = new User();
        user1.setUserName("user1");
        User user2 = new User();
        user2.setUserName("user2");
        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<?> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetAllUsers_noUsersFound() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(null);

        // Act
        ResponseEntity<?> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No users found", response.getBody());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testUpdateUser_success() {
        // Arrange
        String username = "testUser";
        mockAuthentication(username);

        User existingUser = new User();
        existingUser.setId(new ObjectId().toString());
        existingUser.setUserName(username);
        existingUser.setPassword("oldPassword");

        User updatedUserRequest = new User();
        updatedUserRequest.setUserName(username);
        updatedUserRequest.setPassword("newPassword");

        when(userService.findByUserName(username)).thenReturn(existingUser);
        when(userService.saveNewUser(any(User.class))).thenReturn(existingUser);

        // Act
        ResponseEntity<?> response = userController.updateUser(updatedUserRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(existingUser, response.getBody());
        verify(userService, times(1)).findByUserName(username);
        verify(userService, times(1)).saveNewUser(any(User.class));
    }

    @Test
    void testUpdateUser_userNotFound() {
        // Arrange
        String username = "nonExistentUser";
        mockAuthentication(username);

        User updatedUserRequest = new User();
        updatedUserRequest.setUserName(username);
        updatedUserRequest.setPassword("newPassword");

        when(userService.findByUserName(username)).thenReturn(null);

        // Act
        ResponseEntity<?> response = userController.updateUser(updatedUserRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(userService, times(1)).findByUserName(username);
        verify(userService, never()).saveNewUser(any(User.class));
    }

    @Test
    void testDeleteUser_success() {
        // Arrange
        String username = "testUser";
        mockAuthentication(username);

        User existingUser = new User();
        existingUser.setId(new ObjectId().toString());
        existingUser.setUserName(username);

        when(userService.findByUserName(username)).thenReturn(existingUser);
        doNothing().when(userService).deleteUserById(existingUser.getId());

        // Act
        ResponseEntity<?> response = userController.deleteUser();

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).findByUserName(username);
        verify(userService, times(1)).deleteUserById(existingUser.getId());
    }

    @Test
    void testDeleteUser_userNotFound() {
        // Arrange
        String username = "nonExistentUser";
        mockAuthentication(username);

        when(userService.findByUserName(username)).thenReturn(null);

        // Act
        ResponseEntity<?> response = userController.deleteUser();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(userService, times(1)).findByUserName(username);
        verify(userService, never()).deleteUserById(any(String.class));
    }
}
