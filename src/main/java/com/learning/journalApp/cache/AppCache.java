package com.learning.journalApp.cache;

import com.learning.journalApp.entity.ConfigJournalAppEntity;
import com.learning.journalApp.repository.ConfigJournalAppRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AppCache {

    public enum keys {
        WEATHER_API_KEY,
        TTS_API_KEY,
    }

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    private Map<String, String> configCache;

    @PostConstruct
    public void init() {
        configCache = new HashMap<>();
        List<ConfigJournalAppEntity> all = configJournalAppRepository.findAll();
        configCache = all.stream()
                .collect(Collectors.toMap(ConfigJournalAppEntity::key, ConfigJournalAppEntity::value));
    }

    public String getConfigValue(String key) {
        return configCache.get(key);
    }
}