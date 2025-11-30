package com.learning.journalApp.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.Filter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilterConfigTest {

    @InjectMocks
    private FilterConfig filterConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBucket4jRateLimitFilterRegistration() {
        // Arrange
        ApplicationContext mockContext = mock(ApplicationContext.class);
        Bucket4jRateLimitFilter mockFilter = mock(Bucket4jRateLimitFilter.class);
        when(mockContext.getBean(Bucket4jRateLimitFilter.class)).thenReturn(mockFilter);
        ReflectionTestUtils.setField(filterConfig, "applicationContext", mockContext);

        // Act
        FilterRegistrationBean<Filter> registrationBean = filterConfig.bucket4jRateLimitFilter();

        // Assert
        assertNotNull(registrationBean);
        assertNotNull(registrationBean.getFilter());
        assertTrue(registrationBean.getFilter() instanceof Bucket4jRateLimitFilter);
        assertTrue(registrationBean.getUrlPatterns().contains("/public/**"));
        assertEquals(1, registrationBean.getOrder());
    }
}
