package com.learning.journalApp.repository;

import com.learning.journalApp.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

@DataMongoTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByUserNameWhenUserExists() {
        // Arrange
        User user = new User();
        user.setUserName("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setRoles(Arrays.asList("USER"));
        userRepository.save(user);

        // Act
        User foundUser = userRepository.findByUserName("testuser");

        // Assert
        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals("testuser", foundUser.getUserName());
        Assertions.assertEquals("test@example.com", foundUser.getEmail());
    }

    @Test
    void testFindByUserNameWhenUserDoesNotExist() {
        // Arrange - No user saved

        // Act
        User foundUser = userRepository.findByUserName("nonexistentuser");

        // Assert
        Assertions.assertNull(foundUser);
    }

    @Test
    void testDeleteByUserName() {
        // Arrange
        User user1 = new User();
        user1.setUserName("user1");
        user1.setPassword("password");
        user1.setEmail("user1@example.com");
        user1.setRoles(Arrays.asList("USER"));
        userRepository.save(user1);

        User user2 = new User();
        user2.setUserName("user2");
        user2.setPassword("password");
        user2.setEmail("user2@example.com");
        user2.setRoles(Arrays.asList("ADMIN"));
        userRepository.save(user2);

        Assertions.assertEquals(2, userRepository.count());

        // Act
        userRepository.deleteByUserName("user1");

        // Assert
        Assertions.assertEquals(1, userRepository.count());
        User remainingUser = userRepository.findByUserName("user2");
        Assertions.assertNotNull(remainingUser);
        Assertions.assertNull(userRepository.findByUserName("user1"));
    }
}