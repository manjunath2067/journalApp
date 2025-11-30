package com.learning.journalApp.service;

import com.learning.journalApp.entity.JournalEntry;
import com.learning.journalApp.entity.User;
import com.learning.journalApp.repository.JournalEntryRepository;
import com.learning.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JournalEntryServiceTest {

    @Mock
    private JournalEntryRepository journalEntryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JournalEntryService journalEntryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveEntry_Success() {
        // Arrange
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setTitle("My Title");
        journalEntry.setContent("My Content");
        String userName = "testUser";

        User user = new User();
        user.setUserName(userName);
        user.setJournalEntries(new ArrayList<>());

        JournalEntry savedEntry = new JournalEntry();
        savedEntry.setId(new ObjectId());
        savedEntry.setTitle("My Title");
        savedEntry.setContent("My Content");
        savedEntry.setDate(LocalDateTime.now());

        when(userRepository.findByUserName(userName)).thenReturn(user);
        when(journalEntryRepository.save(any(JournalEntry.class))).thenReturn(savedEntry);

        // Act
        journalEntryService.saveEntry(journalEntry, userName);

        // Assert
        verify(userRepository, times(1)).findByUserName(userName);
        verify(journalEntryRepository, times(1)).save(journalEntry);
        verify(userRepository, times(1)).save(user);
        assertNotNull(journalEntry.getDate());
        assertTrue(user.getJournalEntries().contains(savedEntry));
    }

    @Test
    void testSaveEntry_UserNotFound() {
        // Arrange
        JournalEntry journalEntry = new JournalEntry();
        String userName = "nonExistentUser";

        when(userRepository.findByUserName(userName)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> journalEntryService.saveEntry(journalEntry, userName)); // Expecting NullPointerException because user is null
        verify(userRepository, times(1)).findByUserName(userName);
        verify(journalEntryRepository, never()).save(any(JournalEntry.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetAll_ReturnsAllEntries() {
        // Arrange
        List<JournalEntry> expectedEntries = Arrays.asList(new JournalEntry(), new JournalEntry());
        when(journalEntryRepository.findAll()).thenReturn(expectedEntries);

        // Act
        List<JournalEntry> actualEntries = journalEntryService.getAll();

        // Assert
        assertEquals(expectedEntries.size(), actualEntries.size());
        assertEquals(expectedEntries, actualEntries);
        verify(journalEntryRepository, times(1)).findAll();
    }

    @Test
    void testFindById_EntryFound() {
        // Arrange
        ObjectId id = new ObjectId();
        JournalEntry expectedEntry = new JournalEntry();
        when(journalEntryRepository.findById(id)).thenReturn(Optional.of(expectedEntry));

        // Act
        Optional<JournalEntry> actualEntry = journalEntryService.findById(id);

        // Assert
        assertTrue(actualEntry.isPresent());
        assertEquals(expectedEntry, actualEntry.get());
        verify(journalEntryRepository, times(1)).findById(id);
    }

    @Test
    void testFindById_EntryNotFound() {
        // Arrange
        ObjectId id = new ObjectId();
        when(journalEntryRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<JournalEntry> actualEntry = journalEntryService.findById(id);

        // Assert
        assertFalse(actualEntry.isPresent());
        verify(journalEntryRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteById_Success() {
        // Arrange
        ObjectId entryId = new ObjectId();
        String userName = "testUser";

        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setId(entryId);

        User user = new User();
        user.setUserName(userName);
        user.setJournalEntries(new ArrayList<>(List.of(journalEntry)));

        when(userRepository.findByUserName(userName)).thenReturn(user);
        doNothing().when(journalEntryRepository).deleteById(entryId);

        // Act
        journalEntryService.deleteById(entryId, userName);

        // Assert
        verify(userRepository, times(1)).findByUserName(userName);
        verify(userRepository, times(1)).save(user);
        verify(journalEntryRepository, times(1)).deleteById(entryId);
        assertFalse(user.getJournalEntries().contains(journalEntry));
    }

    @Test
    void testDeleteById_EntryNotFoundForUser() {
        // Arrange
        ObjectId entryId = new ObjectId();
        String userName = "testUser";

        JournalEntry otherJournalEntry = new JournalEntry();
        otherJournalEntry.setId(new ObjectId()); // Different ID

        User user = new User();
        user.setUserName(userName);
        user.setJournalEntries(new ArrayList<>(List.of(otherJournalEntry)));

        when(userRepository.findByUserName(userName)).thenReturn(user);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> journalEntryService.deleteById(entryId, userName));
        assertEquals("Journal entry not found for user: " + userName, thrown.getMessage());
        verify(userRepository, times(1)).findByUserName(userName);
        verify(userRepository, never()).save(user);
        verify(journalEntryRepository, never()).deleteById(entryId);
        assertTrue(user.getJournalEntries().contains(otherJournalEntry)); // Ensure other entries are untouched
    }

    @Test
    void testDeleteById_UserNotFound() {
        // Arrange
        ObjectId entryId = new ObjectId();
        String userName = "nonExistentUser";

        when(userRepository.findByUserName(userName)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> journalEntryService.deleteById(entryId, userName));
        verify(userRepository, times(1)).findByUserName(userName);
        verify(userRepository, never()).save(any(User.class));
        verify(journalEntryRepository, never()).deleteById(any(ObjectId.class));
    }


    @Test
    void testUpdateEntry_Success() {
        // Arrange
        ObjectId entryId = new ObjectId();
        String userName = "testUser";

        JournalEntry existingEntry = new JournalEntry();
        existingEntry.setId(entryId);
        existingEntry.setTitle("Old Title");
        existingEntry.setContent("Old Content");
        existingEntry.setDate(LocalDateTime.now().minusDays(1));

        JournalEntry newEntry = new JournalEntry();
        newEntry.setId(entryId);
        newEntry.setTitle("New Title");
        newEntry.setContent("New Content");

        User user = new User();
        user.setUserName(userName);
        user.setJournalEntries(new ArrayList<>(List.of(existingEntry)));

        when(userRepository.findByUserName(userName)).thenReturn(user);
        when(journalEntryRepository.save(any(JournalEntry.class))).thenReturn(existingEntry);

        // Act
        journalEntryService.updateEntry(newEntry, userName);

        // Assert
        verify(userRepository, times(1)).findByUserName(userName);
        verify(journalEntryRepository, times(1)).save(existingEntry);
        assertEquals("New Title", existingEntry.getTitle());
        assertEquals("New Content", existingEntry.getContent());
        assertNotNull(existingEntry.getDate()); // Date should be updated
        assertTrue(existingEntry.getDate().isAfter(LocalDateTime.now().minusMinutes(1))); // Ensure date is recent
    }

    @Test
    void testUpdateEntry_EntryNotFoundInUserJournals() {
        // Arrange
        ObjectId existingEntryId = new ObjectId();
        ObjectId nonExistentEntryId = new ObjectId();
        String userName = "testUser";

        JournalEntry existingEntry = new JournalEntry();
        existingEntry.setId(existingEntryId);
        existingEntry.setTitle("Old Title");
        existingEntry.setContent("Old Content");

        JournalEntry newEntry = new JournalEntry();
        newEntry.setId(nonExistentEntryId); // ID not present in user's journals
        newEntry.setTitle("New Title");
        newEntry.setContent("New Content");

        User user = new User();
        user.setUserName(userName);
        user.setJournalEntries(new ArrayList<>(List.of(existingEntry)));

        when(userRepository.findByUserName(userName)).thenReturn(user);
        when(journalEntryRepository.save(any(JournalEntry.class))).thenReturn(existingEntry); // Still mock save for existing entry

        // Act
        journalEntryService.updateEntry(newEntry, userName);

        // Assert
        verify(userRepository, times(1)).findByUserName(userName);
        verify(journalEntryRepository, never()).save(newEntry); // Should not save the newEntry
        assertEquals("Old Title", existingEntry.getTitle()); // Existing entry should be unchanged
        assertEquals("Old Content", existingEntry.getContent());
    }

    @Test
    void testUpdateEntry_UserNotFound() {
        // Arrange
        JournalEntry newEntry = new JournalEntry();
        String userName = "nonExistentUser";

        when(userRepository.findByUserName(userName)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> journalEntryService.updateEntry(newEntry, userName));
        verify(userRepository, times(1)).findByUserName(userName);
        verify(journalEntryRepository, never()).save(any(JournalEntry.class));
    }
}
