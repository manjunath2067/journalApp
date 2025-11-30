package com.learning.journalApp.controller;

import com.learning.journalApp.entity.JournalEntry;
import com.learning.journalApp.entity.User;
import com.learning.journalApp.service.JournalEntryService;
import com.learning.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class JournalEntityControllerV2Test {

    @InjectMocks
    private JournalEntityControllerV2 journalEntityControllerV2;

    @Mock
    private JournalEntryService journalEntryService;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockAuthentication(String username) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
    }

    @Test
    void testGetAllJournalEntriesOfUser() {
        // Arrange
        String username = "testUser";
        mockAuthentication(username);

        User user = new User();
        user.setUserName(username);
        JournalEntry journalEntry1 = new JournalEntry();
        journalEntry1.setTitle("Title 1");
        JournalEntry journalEntry2 = new JournalEntry();
        journalEntry2.setTitle("Title 2");
        user.setJournalEntries(Arrays.asList(journalEntry1, journalEntry2));

        when(userService.findByUserName(username)).thenReturn(user);

        // Act
        ResponseEntity<List<JournalEntry>> response = journalEntityControllerV2.getAllJournalEntriesOfUser();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAllJournalEntriesOfUser_noUser() {
        // Arrange
        String username = "testUser";
        mockAuthentication(username);
        when(userService.findByUserName(username)).thenReturn(null);

        // Act
        ResponseEntity<List<JournalEntry>> response = journalEntityControllerV2.getAllJournalEntriesOfUser();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testGetJournalEntryById() {
        // Arrange
        String id = new ObjectId().toString();
        String username = "testUser";
        mockAuthentication(username);

        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setTitle("Test Title");
        when(journalEntryService.findByIdAndUserName(id, username)).thenReturn(Optional.of(journalEntry));

        // Act
        ResponseEntity<JournalEntry> response = journalEntityControllerV2.getJournalEntryById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(journalEntry, response.getBody());
    }

    @Test
    void testGetJournalEntryById_notFound() {
        // Arrange
        String id = new ObjectId().toString();
        String username = "testUser";
        mockAuthentication(username);

        when(journalEntryService.findByIdAndUserName(id, username)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<JournalEntry> response = journalEntityControllerV2.getJournalEntryById(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateEntry() {
        // Arrange
        String username = "testUser";
        mockAuthentication(username);

        JournalEntry newEntry = new JournalEntry();
        newEntry.setTitle("New Entry");
        newEntry.setContent("Content");

        User user = new User();
        user.setUserName(username);
        when(userService.findByUserName(username)).thenReturn(user);
        when(journalEntryService.saveEntry(newEntry, username)).thenReturn(newEntry);

        // Act
        ResponseEntity<JournalEntry> response = journalEntityControllerV2.createEntry(newEntry);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newEntry, response.getBody());
        verify(journalEntryService, times(1)).saveEntry(newEntry, username);
    }

    @Test
    void testCreateEntry_noUser() {
        // Arrange
        String username = "testUser";
        mockAuthentication(username);

        JournalEntry newEntry = new JournalEntry();
        newEntry.setTitle("New Entry");
        newEntry.setContent("Content");

        when(userService.findByUserName(username)).thenReturn(null);

        // Act
        ResponseEntity<JournalEntry> response = journalEntityControllerV2.createEntry(newEntry);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(journalEntryService, never()).saveEntry(any(JournalEntry.class), any(String.class));
    }

    @Test
    void testUpdateJournalEntry() {
        // Arrange
        String id = new ObjectId().toString();
        String username = "testUser";
        mockAuthentication(username);

        JournalEntry existingEntry = new JournalEntry();
        existingEntry.setId(new ObjectId(id));
        existingEntry.setTitle("Old Title");
        existingEntry.setContent("Old Content");

        JournalEntry updatedEntryRequest = new JournalEntry();
        updatedEntryRequest.setTitle("Updated Title");
        updatedEntryRequest.setContent("Updated Content");

        when(journalEntryService.findByIdAndUserName(id, username)).thenReturn(Optional.of(existingEntry));
        when(journalEntryService.saveEntry(any(JournalEntry.class))).thenReturn(existingEntry);

        // Act
        ResponseEntity<JournalEntry> response = journalEntityControllerV2.updateJournalEntry(id, updatedEntryRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Title", response.getBody().getTitle());
        assertEquals("Updated Content", response.getBody().getContent());
    }

    @Test
    void testUpdateJournalEntry_notFound() {
        // Arrange
        String id = new ObjectId().toString();
        String username = "testUser";
        mockAuthentication(username);

        JournalEntry updatedEntryRequest = new JournalEntry();
        updatedEntryRequest.setTitle("Updated Title");

        when(journalEntryService.findByIdAndUserName(id, username)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<JournalEntry> response = journalEntityControllerV2.updateJournalEntry(id, updatedEntryRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(journalEntryService, never()).saveEntry(any(JournalEntry.class));
    }

    @Test
    void testDeleteJournalEntryById() {
        // Arrange
        String id = new ObjectId().toString();
        String username = "testUser";
        mockAuthentication(username);

        when(journalEntryService.deleteByIdAndUserName(id, username)).thenReturn(true);

        // Act
        ResponseEntity<?> response = journalEntityControllerV2.deleteJournalEntryById(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(journalEntryService, times(1)).deleteByIdAndUserName(id, username);
    }

    @Test
    void testDeleteJournalEntryById_notFound() {
        // Arrange
        String id = new ObjectId().toString();
        String username = "testUser";
        mockAuthentication(username);

        when(journalEntryService.deleteByIdAndUserName(id, username)).thenReturn(false);

        // Act
        ResponseEntity<?> response = journalEntityControllerV2.deleteJournalEntryById(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(journalEntryService, times(1)).deleteByIdAndUserName(id, username);
    }
}
