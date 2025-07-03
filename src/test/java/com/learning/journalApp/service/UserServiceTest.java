package com.learning.journalApp.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.learning.journalApp.client.WeatherClient;
import com.learning.journalApp.entity.User;
import com.learning.journalApp.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @MockBean // Changed from @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private WeatherClient weatherClient;

    @ParameterizedTest
    @ArgumentsSource(UserArgumentProvider.class)
    public void testSaveNewUser(User user) {
        // Mock behavior for userRepository.save if it's called within saveNewUser
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        when(weatherClient.getWeather(Mockito.anyString(), Mockito.anyString()))
              .thenReturn(null);

        assertTrue(userService.saveNewUser(user));
        // userRepository.delete(user); // Removed as userRepository is now a mock

        // Verify that save was called on the mock repository
        Mockito.verify(userRepository).save(user);
    }
}
