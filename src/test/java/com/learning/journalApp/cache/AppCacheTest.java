package com.learning.journalApp.cache;

import com.learning.journalApp.entity.ConfigJournalAppEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class AppCacheTest {

    private AppCache appCache;

    @BeforeEach
    void setUp() {
        appCache = new AppCache();
        appCache.init();
    }

    @Test
    void testWeatherDetailCacheInitialization() {
        assertNotNull(appCache.weatherDetailCache, "Weather detail cache should not be null after initialization");
        appCache.weatherDetailCache.put(AppCache.WEATHER_CACHE, "Sunny");
        assertEquals("Sunny", appCache.weatherDetailCache.getIfPresent(AppCache.WEATHER_CACHE), "Should retrieve the value from weather cache");
    }

    @Test
    void testJournalAppConfigurationCacheInitialization() {
        assertNotNull(appCache.journalAppConfigurationCache, "Journal App configuration cache should not be null after initialization");
        ConfigJournalAppEntity config = new ConfigJournalAppEntity();
        config.setKey("testKey");
        config.setValue("testValue");
        appCache.journalAppConfigurationCache.put(AppCache.JOURNAL_APP_CONFIGURATION_CACHE, config);
        assertEquals(config, appCache.journalAppConfigurationCache.getIfPresent(AppCache.JOURNAL_APP_CONFIGURATION_CACHE), "Should retrieve the config from journal app configuration cache");
    }

    @Test
    void testWeatherDetailCacheExpiration() throws InterruptedException {
        appCache.weatherDetailCache = com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS).maximumSize(100).build();
        appCache.weatherDetailCache.put("testKey", "testValue");
        Thread.sleep(1100);
        assertNull(appCache.weatherDetailCache.getIfPresent("testKey"), "Weather cache entry should expire");
    }

    @Test
    void testJournalAppConfigurationCacheExpiration() throws InterruptedException {
        appCache.journalAppConfigurationCache = com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS).maximumSize(1).build();
        ConfigJournalAppEntity config = new ConfigJournalAppEntity();
        config.setKey("testConfig");
        appCache.journalAppConfigurationCache.put("testConfig", config);
        Thread.sleep(1100);
        assertNull(appCache.journalAppConfigurationCache.getIfPresent("testConfig"), "Journal app configuration cache entry should expire");
    }

    @Test
    void testInitMethodResetsCaches() {
        appCache.weatherDetailCache.put("initialKey", "initialValue");
        ConfigJournalAppEntity config = new ConfigJournalAppEntity();
        appCache.journalAppConfigurationCache.put("initialConfig", config);

        appCache.init();

        assertNull(appCache.weatherDetailCache.getIfPresent("initialKey"), "Calling init should clear the previous weather cache instance");
        assertNull(appCache.journalAppConfigurationCache.getIfPresent("initialConfig"), "Calling init should clear the previous configuration cache instance");
        assertNotNull(appCache.weatherDetailCache, "Weather cache should be reinitialized and not null");
        assertNotNull(appCache.journalAppConfigurationCache, "Journal app configuration cache should be reinitialized and not null");
    }
}