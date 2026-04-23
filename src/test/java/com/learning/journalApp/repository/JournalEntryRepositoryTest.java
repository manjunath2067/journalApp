package com.learning.journalApp.repository;

import com.learning.journalApp.entity.JournalEntry;
import com.learning.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ActiveProfiles("test")
public class JournalEntryRepositoryTest {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserRepository userRepository; // To save user for JournalEntry

    private User testUser;

    @BeforeEach
    public void setup() {
        journalEntryRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setPassword("testpassword");
        testUser.setEmail("test@example.com");
        testUser.setRoles(Arrays.asList("USER"));
        userRepository.save(testUser);
    }

    @AfterEach
    public void teardown() {
        journalEntryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindByUserWhenEntriesExist() {
        // Arrange
        JournalEntry entry1 = new JournalEntry();
        entry1.setTitle("Test Title 1");
        entry1.setContent("Test Content 1");
        entry1.setDate(LocalDateTime.now());
        entry1.setUser(testUser);
        journalEntryRepository.save(entry1);

        JournalEntry entry2 = new JournalEntry();
        entry2.setTitle("Test Title 2");
        entry2.setContent("Test Content 2");
        entry2.setDate(LocalDateTime.now().plusDays(1));
        entry2.setUser(testUser);
        journalEntryRepository.save(entry2);

        // Act
        List<JournalEntry> foundEntries = journalEntryRepository.findByUser(testUser);

        // Assert
        Assertions.assertNotNull(foundEntries);
        Assertions.assertEquals(2, foundEntries.size());
        Assertions.assertTrue(foundEntries.stream().anyMatch(e -> e.getTitle().equals("Test Title 1")));
        Assertions.assertTrue(foundEntries.stream().anyMatch(e -> e.getTitle().equals("Test Title 2")));
    }

    @Test
    void testFindByUserWhenNoEntriesExist() {
        // Arrange - No journal entries saved for testUser

        // Act
        List<JournalEntry> foundEntries = journalEntryRepository.findByUser(testUser);

        // Assert
        Assertions.assertNotNull(foundEntries);
        Assertions.assertTrue(foundEntries.isEmpty());
    }

    @Test
    void testDeleteAllByUser() {
        // Arrange
        JournalEntry entry1 = new JournalEntry();
        entry1.setTitle("Test Title 1");
        entry1.setContent("Test Content 1");
        entry1.setDate(LocalDateTime.now());
        entry1.setUser(testUser);
        journalEntryRepository.save(entry1);

        JournalEntry entry2 = new JournalEntry();
        entry2.setTitle("Test Title 2");
        entry2.setContent("Test Content 2");
        entry2.setDate(LocalDateTime.now().plusDays(1));
        entry2.setUser(testUser);
        journalEntryRepository.save(entry2);

        // Create another user and entry to ensure only testUser's entries are deleted
        User otherUser = new User();
        otherUser.setUserName("otheruser");
        otherUser.setPassword("otherpassword");
        otherUser.setEmail("other@example.com");
        otherUser.setRoles(Arrays.asList("USER"));
        userRepository.save(otherUser);

        JournalEntry otherUserEntry = new JournalEntry();
        otherUserEntry.setTitle("Other User Entry");
        otherUserEntry.setContent("Content for other user");
        otherUserEntry.setDate(LocalDateTime.now());
        otherUserEntry.setUser(otherUser);
        journalEntryRepository.save(otherUserEntry);

        Assertions.assertEquals(3, journalEntryRepository.count()); // 2 for testUser, 1 for otherUser

        // Act
        journalEntryRepository.deleteAllByuser(testUser);

        // Assert
        List<JournalEntry> remainingEntries = journalEntryRepository.findAll();
        Assertions.assertEquals(1, remainingEntries.size());
        Assertions.assertEquals("Other User Entry", remainingEntries.get(0).getTitle());
        Assertions.assertEquals(otherUser.getUserName(), remainingEntries.get(0).getUser().getUserName());

        // Verify testUser's entries are gone
        List<JournalEntry> testUserEntriesAfterDelete = journalEntryRepository.findByUser(testUser);
        Assertions.assertTrue(testUserEntriesAfterDelete.isEmpty());
    }
}