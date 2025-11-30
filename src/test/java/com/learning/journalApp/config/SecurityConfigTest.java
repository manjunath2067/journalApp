package com.learning.journalApp.config;

import com.learning.journalApp.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setUp() {
        // No explicit setup needed as @InjectMocks and @Mock handle injections
    }

    @Test
    void testPasswordEncoderBean() {
        // Act
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void testAuthenticationProviderBean() {
        // Act
        DaoAuthenticationProvider authenticationProvider = securityConfig.authenticationProvider(securityConfig.passwordEncoder());

        // Assert
        assertNotNull(authenticationProvider);
        assertNotNull(authenticationProvider.getUserDetailsService());
        assertNotNull(authenticationProvider.getPasswordEncoder());
    }

    @Test
    void testAuthenticationManagerBean() throws Exception {
        // Arrange
        AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        // Act
        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        // Assert
        assertNotNull(result);
        assertEquals(authenticationManager, result);
    }

    @Test
    void testSecurityFilterChain() throws Exception {
        // Arrange
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        when(httpSecurity.csrf(any(AbstractHttpConfigurer.class)).and().authorizeHttpRequests(any()).sessionManagement(any()).authenticationProvider(any()).addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class)).build()).thenReturn(mock(SecurityFilterChain.class));

        // Act
        SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity);

        // Assert
        assertNotNull(filterChain);
        verify(httpSecurity).csrf(any(AbstractHttpConfigurer.class));
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).authenticationProvider(any());
        verify(httpSecurity).addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class));
    }
}
