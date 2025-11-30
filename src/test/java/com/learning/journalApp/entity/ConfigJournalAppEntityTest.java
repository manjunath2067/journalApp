package com.learning.journalApp.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConfigJournalAppEntityTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        ConfigJournalAppEntity entity = new ConfigJournalAppEntity();

        // Act
        entity.setId("testId");
        entity.setKey("testKey");
        entity.setValue("testValue");

        // Assert
        assertEquals("testId", entity.getId());
        assertEquals("testKey", entity.getKey());
        assertEquals("testValue", entity.getValue());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        ConfigJournalAppEntity entity = new ConfigJournalAppEntity();

        // Assert
        assertNull(entity.getId());
        assertNull(entity.getKey());
        assertNull(entity.getValue());
    }
}
