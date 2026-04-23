package com.learning.journalApp.repository;

import com.learning.journalApp.entity.ConfigJournalAppEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataMongoTest
@ActiveProfiles("test")
public class ConfigJournalAppRepositoryTest {

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    @BeforeEach
    public void setup() {
        configJournalAppRepository.deleteAll();
    }

    @Test
    void testFindByKeyWhenKeyExists() {
        // Arrange
        ConfigJournalAppEntity entity = new ConfigJournalAppEntity();
        entity.setKey("WEATHER_API_KEY");
        entity.setValue("testApiKey123");
        configJournalAppRepository.save(entity);

        // Act
        Optional<ConfigJournalAppEntity> foundEntity = configJournalAppRepository.findByKey("WEATHER_API_KEY");

        // Assert
        Assertions.assertTrue(foundEntity.isPresent());
        Assertions.assertEquals("testApiKey123", foundEntity.get().getValue());
    }

    @Test
    void testFindByKeyWhenKeyDoesNotExist() {
        // Arrange - No entity saved

        // Act
        Optional<ConfigJournalAppEntity> foundEntity = configJournalAppRepository.findByKey("NON_EXISTENT_KEY");

        // Assert
        Assertions.assertFalse(foundEntity.isPresent());
    }
}