package com.learning.journalApp.service;

import com.learning.journalApp.entity.JournalEntry;
import com.learning.journalApp.entity.User;
import com.learning.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Alternative to MockitoAnnotations.openMocks(this)
public class JournalEntryServiceTests {

    @InjectMocks
    private JournalEntryService journalEntryService;

    @Mock
    private JournalEntryRepository journalEntryRepository;

    @Mock
    private UserService userService;

    private User testUser;
    private JournalEntry testJournalEntry;
    private ObjectId testJournalEntryId;
    private String testUsername = "testUser";

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this); // Not needed if using @ExtendWith(MockitoExtension.class)

        testUser = User.builder()
                .userName(testUsername)
                .journalEntries(new ArrayList<>()) // Initialize with an empty mutable list
                .roles(new ArrayList<>()) // Assuming roles might be needed/used
                .build();

        testJournalEntryId = new ObjectId();
        testJournalEntry = new JournalEntry();
        testJournalEntry.setId(testJournalEntryId);
        testJournalEntry.setTitle("Test Title");
        testJournalEntry.setContent("Test Content");
    }

    // Tests for saveEntry(JournalEntry journalEntry, String userName)
    @Test
    void testSaveEntryWithUsername_success() {
        // Arrange
        when(userService.findByUserName(testUsername)).thenReturn(testUser);
        when(journalEntryRepository.save(any(JournalEntry.class))).thenReturn(testJournalEntry);
        // userService.saveUser(user) is void, so no need to mock its return for happy path unless verifying call

        // Act
        journalEntryService.saveEntry(testJournalEntry, testUsername);

        // Assert
        assertNotNull(testJournalEntry.getDate());
        verify(userService, times(1)).findByUserName(testUsername);
        verify(journalEntryRepository, times(1)).save(testJournalEntry);
        verify(userService, times(1)).saveUser(testUser);
        assertTrue(testUser.getJournalEntries().contains(testJournalEntry));
    }

    @Test
    void testSaveEntryWithUsername_userJournalEntriesNull_initializesList() {
        // Arrange
        testUser.setJournalEntries(null); // Simulate user's journal list being null initially
        when(userService.findByUserName(testUsername)).thenReturn(testUser);
        when(journalEntryRepository.save(any(JournalEntry.class))).thenReturn(testJournalEntry);

        // Act
        journalEntryService.saveEntry(testJournalEntry, testUsername);

        // Assert
        assertNotNull(testUser.getJournalEntries());
        assertFalse(testUser.getJournalEntries().isEmpty());
        assertTrue(testUser.getJournalEntries().contains(testJournalEntry));
        verify(userService, times(1)).saveUser(testUser);
    }

    @Test
    void testSaveEntryWithUsername_userServiceFindFails_throwsRuntimeException() {
        // Arrange
        when(userService.findByUserName(testUsername)).thenThrow(new RuntimeException("User service find error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            journalEntryService.saveEntry(testJournalEntry, testUsername);
        });
        assertTrue(exception.getMessage().contains("An error occurred while saving the journal entry"));
        assertTrue(exception.getMessage().contains("User service find error"));
        verify(journalEntryRepository, never()).save(any(JournalEntry.class));
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void testSaveEntryWithUsername_repoSaveFails_throwsRuntimeException() {
        // Arrange
        when(userService.findByUserName(testUsername)).thenReturn(testUser);
        when(journalEntryRepository.save(any(JournalEntry.class))).thenThrow(new RuntimeException("Repo save error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            journalEntryService.saveEntry(testJournalEntry, testUsername);
        });
        assertTrue(exception.getMessage().contains("An error occurred while saving the journal entry"));
        assertTrue(exception.getMessage().contains("Repo save error"));
        verify(userService, never()).saveUser(any(User.class)); // Should not reach user save if repo save fails
    }

    // Tests for saveEntry(JournalEntry journalEntry)
    @Test
    void testSaveEntrySimple_success() {
        // Arrange
        // No specific arrangement needed beyond mocks

        // Act
        journalEntryService.saveEntry(testJournalEntry);

        // Assert
        verify(journalEntryRepository, times(1)).save(testJournalEntry);
    }

    // Tests for getAll()
    @Test
    void testGetAll_success() {
        // Arrange
        List<JournalEntry> expectedEntries = List.of(testJournalEntry, new JournalEntry());
        when(journalEntryRepository.findAll()).thenReturn(expectedEntries);

        // Act
        List<JournalEntry> actualEntries = journalEntryService.getAll();

        // Assert
        assertEquals(expectedEntries, actualEntries);
        verify(journalEntryRepository, times(1)).findAll();
    }

    // Tests for getJournalEntryById(ObjectId id)
    @Test
    void testGetJournalEntryById_found() {
        // Arrange
        when(journalEntryRepository.findById(testJournalEntryId)).thenReturn(Optional.of(testJournalEntry));

        // Act
        Optional<JournalEntry> actualEntryOpt = journalEntryService.getJournalEntryById(testJournalEntryId);

        // Assert
        assertTrue(actualEntryOpt.isPresent());
        assertEquals(testJournalEntry, actualEntryOpt.get());
        verify(journalEntryRepository, times(1)).findById(testJournalEntryId);
    }

    @Test
    void testGetJournalEntryById_notFound() {
        // Arrange
        when(journalEntryRepository.findById(testJournalEntryId)).thenReturn(Optional.empty());

        // Act
        Optional<JournalEntry> actualEntryOpt = journalEntryService.getJournalEntryById(testJournalEntryId);

        // Assert
        assertTrue(actualEntryOpt.isEmpty());
        verify(journalEntryRepository, times(1)).findById(testJournalEntryId);
    }

    // Tests for deleteJournalEntryById(ObjectId id, String userName)
    @Test
    void testDeleteJournalEntryById_success() {
        // Arrange
        testUser.getJournalEntries().add(testJournalEntry); // Add entry to user's list
        when(userService.findByUserName(testUsername)).thenReturn(testUser);
        // journalEntryRepository.deleteById is void

        // Act
        boolean result = journalEntryService.deleteJournalEntryById(testJournalEntryId, testUsername);

        // Assert
        assertTrue(result);
        assertFalse(testUser.getJournalEntries().contains(testJournalEntry));
        verify(userService, times(1)).findByUserName(testUsername);
        verify(userService, times(1)).saveUser(testUser);
        verify(journalEntryRepository, times(1)).deleteById(testJournalEntryId);
    }

    @Test
    void testDeleteJournalEntryById_entryNotInUserList() {
        // Arrange
        // testJournalEntry is NOT added to testUser.getJournalEntries()
        when(userService.findByUserName(testUsername)).thenReturn(testUser);

        // Act
        boolean result = journalEntryService.deleteJournalEntryById(testJournalEntryId, testUsername);

        // Assert
        assertFalse(result);
        verify(userService, times(1)).findByUserName(testUsername);
        verify(userService, never()).saveUser(testUser);
        verify(journalEntryRepository, never()).deleteById(testJournalEntryId);
    }

    @Test
    void testDeleteJournalEntryById_userServiceFindFails_throwsRuntimeException() {
        // Arrange
        when(userService.findByUserName(testUsername)).thenThrow(new RuntimeException("User service find error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            journalEntryService.deleteJournalEntryById(testJournalEntryId, testUsername);
        });
        assertTrue(exception.getMessage().contains("An error occurred while deleting the journal entry"));
        assertTrue(exception.getMessage().contains("User service find error"));
        verify(journalEntryRepository, never()).deleteById(any(ObjectId.class));
    }

    @Test
    void testDeleteJournalEntryById_repoDeleteFails_throwsRuntimeException() {
        // Arrange
        testUser.getJournalEntries().add(testJournalEntry);
        when(userService.findByUserName(testUsername)).thenReturn(testUser);
        doThrow(new RuntimeException("Repo delete error")).when(journalEntryRepository).deleteById(testJournalEntryId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            journalEntryService.deleteJournalEntryById(testJournalEntryId, testUsername);
        });
        // The exception is caught by the service's generic catch block
        assertTrue(exception.getMessage().contains("An error occurred while deleting the journal entry"));
        assertTrue(exception.getMessage().contains("Repo delete error"));

        // User save would have been called before deleteById in the original code if entry was removed from list
        verify(userService, times(1)).saveUser(testUser);
    }
}
