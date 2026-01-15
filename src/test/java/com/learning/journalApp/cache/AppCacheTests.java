package com.learning.journalApp.cache;

import com.learning.journalApp.entity.ConfigJournalAppEntity;
import com.learning.journalApp.repository.ConfigJournalAppRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppCacheTests {

    @InjectMocks
    private AppCache appCache;

    @Mock
    private ConfigJournalAppRepository configJournalAppRepository;

    // @BeforeEach setUp method removed

    @Test
    void testInit_populatesCacheFromRepository() {
        // Arrange
        List<ConfigJournalAppEntity> mockConfigList = new ArrayList<>();
        ConfigJournalAppEntity apiKeyEntry = new ConfigJournalAppEntity("WEATHER_API_KEY", "test_api_key_value");
        mockConfigList.add(apiKeyEntry);

        ConfigJournalAppEntity anotherEntry = new ConfigJournalAppEntity("TTS_API_KEY", "test_tts_key_value");
        mockConfigList.add(anotherEntry);

        when(configJournalAppRepository.findAll()).thenReturn(mockConfigList);

        // Act
        appCache.init(); // Manually call @PostConstruct method for testing

        // Assert
        verify(configJournalAppRepository, times(1)).findAll();
        assertEquals("test_api_key_value", appCache.getConfigValue("WEATHER_API_KEY"));
        assertEquals("test_tts_key_value", appCache.getConfigValue("TTS_API_KEY"));
    }

    @Test
    void testInit_handlesEmptyRepository() {
        // Arrange
        when(configJournalAppRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        appCache.init();

        // Assert
        verify(configJournalAppRepository, times(1)).findAll();
        assertNull(appCache.getConfigValue("ANY_KEY")); // Cache should be empty
    }

    @Test
    void testGetConfigValue_keyExists() {
        // Arrange
        // Populate cache via init()
        List<ConfigJournalAppEntity> mockConfigList = new ArrayList<>();
        ConfigJournalAppEntity apiKeyEntry = new ConfigJournalAppEntity("EXISTING_KEY", "expected_value");
        mockConfigList.add(apiKeyEntry);
        when(configJournalAppRepository.findAll()).thenReturn(mockConfigList);
        appCache.init();

        // Act
        String actualValue = appCache.getConfigValue("EXISTING_KEY");

        // Assert
        assertEquals("expected_value", actualValue);
    }

    @Test
    void testGetConfigValue_keyDoesNotExist() {
        // Arrange
        // Populate cache via init() with some other data or empty
        when(configJournalAppRepository.findAll()).thenReturn(new ArrayList<>());
        appCache.init();

        // Act
        String actualValue = appCache.getConfigValue("NON_EXISTING_KEY");

        // Assert
        assertNull(actualValue);
    }

    @Test
    void testGetConfigValue_keyEnumToString() {
        // Arrange
        List<ConfigJournalAppEntity> mockConfigList = new ArrayList<>();
        ConfigJournalAppEntity apiKeyEntry = new ConfigJournalAppEntity(AppCache.keys.WEATHER_API_KEY.name(), "weather_enum_value");
        mockConfigList.add(apiKeyEntry);
        when(configJournalAppRepository.findAll()).thenReturn(mockConfigList);
        appCache.init();

        // Act
        String actualValue = appCache.getConfigValue(AppCache.keys.WEATHER_API_KEY.name());

        // Assert
        assertEquals("weather_enum_value", actualValue);
    }
}
