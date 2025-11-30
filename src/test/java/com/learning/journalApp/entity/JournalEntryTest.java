package com.learning.journalApp.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JournalEntryTest {

    @Test
    void testNoArgsConstructor() {
        JournalEntry journalEntry = new JournalEntry();
        assertNull(journalEntry.getId());
        assertNull(journalEntry.getTitle());
        assertNull(journalEntry.getContent());
        assertNull(journalEntry.getDate());
    }

    @Test
    void testGettersAndSetters() {
        JournalEntry journalEntry = new JournalEntry();
        String id = "testId";
        String title = "Test Title";
        String content = "Test Content";
        LocalDateTime date = LocalDateTime.now();

        journalEntry.setId(id);
        journalEntry.setTitle(title);
        journalEntry.setContent(content);
        journalEntry.setDate(date);

        assertEquals(id, journalEntry.getId());
        assertEquals(title, journalEntry.getTitle());
        assertEquals(content, journalEntry.getContent());
        assertEquals(date, journalEntry.getDate());
    }

    @Test
    void testDateInitializationInConstructor() {
        // Given
        JournalEntry journalEntry = new JournalEntry();

        // When
        journalEntry.setTitle("Sample Title");
        journalEntry.setContent("Sample Content");

        // Then
        assertNotNull(journalEntry.getDate());
    }
}
