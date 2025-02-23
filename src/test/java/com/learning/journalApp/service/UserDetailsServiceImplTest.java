package com.learning.journalApp.service;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;

import com.learning.journalApp.entity.User;
import com.learning.journalApp.repository.UserRepository;

import static org.mockito.Mockito.*;

/**
 * Using @SpringBootTest loads all beans, allowing the use of @Autowired. To avoid loading the entire repository in the
 * class, use @InjectMocks. This approach is faster and loads only the required beans, which can be mocked using @Mock.
 */
public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUserNameTest() {
        when(userRepository.findByUserName(ArgumentMatchers.anyString())).thenReturn(User.builder()
              .userName("test")
              .password("test")
              .roles(new ArrayList<>())
              .build());
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername("hello");
        Assertions.assertNotNull(userDetails);

    }

}
