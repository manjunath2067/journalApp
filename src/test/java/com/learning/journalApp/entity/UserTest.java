package com.learning.journalApp.entity;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserTest {

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getUserName());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        assertNull(user.getRoles());
        assertNull(user.getJournalEntries());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        User user = new User();
        String id = "testId";
        String userName = "testUser";
        String password = "testPassword";
        String email = "test@example.com";
        Set<String> roles = new HashSet<>(Arrays.asList("USER", "ADMIN"));
        List<JournalEntry> journalEntries = new ArrayList<>();
        journalEntries.add(new JournalEntry());

        // Act
        user.setId(id);
        user.setUserName(userName);
        user.setPassword(password);
        user.setEmail(email);
        user.setRoles(roles);
        user.setJournalEntries(journalEntries);

        // Assert
        assertEquals(id, user.getId());
        assertEquals(userName, user.getUserName());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(roles, user.getRoles());
        assertEquals(journalEntries, user.getJournalEntries());
    }

    @Test
    void testAddJournalEntry() {
        User user = new User();
        user.setJournalEntries(new ArrayList<>());
        JournalEntry entry = new JournalEntry();
        user.addJournalEntry(entry);
        assertEquals(1, user.getJournalEntries().size());
        assertTrue(user.getJournalEntries().contains(entry));
    }

    @Test
    void testRemoveJournalEntry() {
        User user = new User();
        JournalEntry entry1 = new JournalEntry();
        JournalEntry entry2 = new JournalEntry();
        user.setJournalEntries(new ArrayList<>(Arrays.asList(entry1, entry2)));
        user.removeJournalEntry(entry1);
        assertEquals(1, user.getJournalEntries().size());
        assertTrue(user.getJournalEntries().contains(entry2));
    }
}
