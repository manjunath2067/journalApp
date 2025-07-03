package com.learning.journalApp.controller;

import com.learning.journalApp.entity.JournalEntry;
import com.learning.journalApp.entity.User;
import com.learning.journalApp.service.JournalEntryService;
import com.learning.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JournalEntityControllerV2Tests {

    @InjectMocks
    private JournalEntityControllerV2 journalController;

    @Mock
    private JournalEntryService journalEntryService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private User testUser;
    private String testUsername = "testUser";
    private JournalEntry testJournalEntry;
    private ObjectId testEntryId;

    @BeforeEach
    void setUp() {
        // Setup Spring Security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);

        testUser = User.builder()
                .userName(testUsername)
                .journalEntries(new ArrayList<>())
                .roles(new ArrayList<>())
                .build();

        testEntryId = new ObjectId();
        testJournalEntry = new JournalEntry();
        testJournalEntry.setId(testEntryId);
        testJournalEntry.setTitle("Test Title");
        testJournalEntry.setContent("Test Content");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // --- Tests for getAll() ---
    @Test
    void testGetAll_entriesExist_returnsOk() {
        testUser.getJournalEntries().add(testJournalEntry);
        when(userService.findByUserName(testUsername)).thenReturn(testUser);

        ResponseEntity<?> response = journalController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<JournalEntry> responseBody = (List<JournalEntry>) response.getBody();
        assertEquals(1, responseBody.size());
        assertEquals(testJournalEntry, responseBody.get(0));
        verify(userService, times(1)).findByUserName(testUsername);
    }

    @Test
    void testGetAll_noEntries_returnsNotFound() {
        when(userService.findByUserName(testUsername)).thenReturn(testUser); // User exists but has no entries

        ResponseEntity<?> response = journalController.getAll();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).findByUserName(testUsername);
    }

    @Test
    void testGetAll_userJournalEntriesNull_returnsNotFound() {
        testUser.setJournalEntries(null);
        when(userService.findByUserName(testUsername)).thenReturn(testUser);

        ResponseEntity<?> response = journalController.getAll();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).findByUserName(testUsername);
    }

    // --- Tests for createEntry() ---
    @Test
    void testCreateEntry_success_returnsCreated() {
        // journalEntryService.saveEntry is void
        doNothing().when(journalEntryService).saveEntry(any(JournalEntry.class), eq(testUsername));

        ResponseEntity<JournalEntry> response = journalController.createEntry(testJournalEntry);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testJournalEntry.getTitle(), response.getBody().getTitle());
        assertNotNull(response.getBody().getDate()); // Date should be set by controller
        verify(journalEntryService, times(1)).saveEntry(testJournalEntry, testUsername);
    }

    @Test
    void testCreateEntry_serviceThrowsException_returnsBadRequest() {
        doThrow(new RuntimeException("Service error")).when(journalEntryService).saveEntry(any(JournalEntry.class), eq(testUsername));

        ResponseEntity<JournalEntry> response = journalController.createEntry(testJournalEntry);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(journalEntryService, times(1)).saveEntry(testJournalEntry, testUsername);
    }

    // --- Tests for getJournalEntryById() ---
    @Test
    void testGetJournalEntryById_entryExistsAndOwned_returnsOk() {
        testUser.getJournalEntries().add(testJournalEntry); // User owns this entry
        when(userService.findByUserName(testUsername)).thenReturn(testUser);
        when(journalEntryService.getJournalEntryById(testEntryId)).thenReturn(Optional.of(testJournalEntry));

        ResponseEntity<JournalEntry> response = journalController.getJournalEntryById(testEntryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testJournalEntry, response.getBody());
        verify(userService, times(1)).findByUserName(testUsername);
        verify(journalEntryService, times(1)).getJournalEntryById(testEntryId);
    }

    @Test
    void testGetJournalEntryById_entryNotOwnedByUser_returnsNotFound() {
        // User's list is empty, so they don't own the entry
        when(userService.findByUserName(testUsername)).thenReturn(testUser);
        // journalEntryService.getJournalEntryById should not be called if not in user's list

        ResponseEntity<JournalEntry> response = journalController.getJournalEntryById(testEntryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).findByUserName(testUsername);
        verify(journalEntryService, never()).getJournalEntryById(any(ObjectId.class));
    }

    @Test
    void testGetJournalEntryById_entryOwnedButServiceReturnsEmpty_returnsNotFound() {
        testUser.getJournalEntries().add(testJournalEntry); // User "owns" it by ID
        when(userService.findByUserName(testUsername)).thenReturn(testUser);
        when(journalEntryService.getJournalEntryById(testEntryId)).thenReturn(Optional.empty()); // But service can't find it

        ResponseEntity<JournalEntry> response = journalController.getJournalEntryById(testEntryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).findByUserName(testUsername);
        verify(journalEntryService, times(1)).getJournalEntryById(testEntryId);
    }

    // --- Tests for deleteEntryById() ---
    @Test
    void testDeleteEntryById_success_returnsNoContent() {
        when(journalEntryService.deleteJournalEntryById(testEntryId, testUsername)).thenReturn(true);

        ResponseEntity<?> response = journalController.deleteEntryById(testEntryId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(journalEntryService, times(1)).deleteJournalEntryById(testEntryId, testUsername);
    }

    @Test
    void testDeleteEntryById_serviceReturnsFalse_returnsNotFound() {
        when(journalEntryService.deleteJournalEntryById(testEntryId, testUsername)).thenReturn(false);

        ResponseEntity<?> response = journalController.deleteEntryById(testEntryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(journalEntryService, times(1)).deleteJournalEntryById(testEntryId, testUsername);
    }

    // --- Tests for updateJournalEntryById() ---
    @Test
    void testUpdateJournalEntryById_success_returnsOk() {
        JournalEntry newDetails = new JournalEntry();
        newDetails.setTitle("New Title");
        newDetails.setContent("New Content");

        testUser.getJournalEntries().add(testJournalEntry); // User owns the entry
        when(userService.findByUserName(testUsername)).thenReturn(testUser);
        when(journalEntryService.getJournalEntryById(testEntryId)).thenReturn(Optional.of(testJournalEntry));
        // journalEntryService.saveEntry(old) is void

        ResponseEntity<?> response = journalController.updateJournalEntryById(testEntryId, newDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JournalEntry updatedEntry = (JournalEntry) response.getBody();
        assertNotNull(updatedEntry);
        assertEquals("New Title", updatedEntry.getTitle());
        assertEquals("New Content", updatedEntry.getContent());
        verify(userService, times(1)).findByUserName(testUsername);
        verify(journalEntryService, times(1)).getJournalEntryById(testEntryId);
        verify(journalEntryService, times(1)).saveEntry(testJournalEntry); // Verifies the 'old' (now updated) entry is saved
    }

    @Test
    void testUpdateJournalEntryById_partialUpdate_titleOnly() {
        JournalEntry newDetails = new JournalEntry();
        newDetails.setTitle("New Title Only");
        // newDetails.setContent(null); // or empty, depending on how you want to test

        testUser.getJournalEntries().add(testJournalEntry);
        String originalContent = testJournalEntry.getContent(); // Save original content
        when(userService.findByUserName(testUsername)).thenReturn(testUser);
        when(journalEntryService.getJournalEntryById(testEntryId)).thenReturn(Optional.of(testJournalEntry));

        ResponseEntity<?> response = journalController.updateJournalEntryById(testEntryId, newDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JournalEntry updatedEntry = (JournalEntry) response.getBody();
        assertNotNull(updatedEntry);
        assertEquals("New Title Only", updatedEntry.getTitle());
        assertEquals(originalContent, updatedEntry.getContent()); // Content should remain unchanged
        verify(journalEntryService, times(1)).saveEntry(testJournalEntry);
    }

    @Test
    void testUpdateJournalEntryById_partialUpdate_contentOnly() {
        JournalEntry newDetails = new JournalEntry();
        newDetails.setContent("New Content Only");
        // newDetails.setTitle(null); // or empty

        testUser.getJournalEntries().add(testJournalEntry);
        String originalTitle = testJournalEntry.getTitle();
        when(userService.findByUserName(testUsername)).thenReturn(testUser);
        when(journalEntryService.getJournalEntryById(testEntryId)).thenReturn(Optional.of(testJournalEntry));

        ResponseEntity<?> response = journalController.updateJournalEntryById(testEntryId, newDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JournalEntry updatedEntry = (JournalEntry) response.getBody();
        assertNotNull(updatedEntry);
        assertEquals(originalTitle, updatedEntry.getTitle());
        assertEquals("New Content Only", updatedEntry.getContent());
        verify(journalEntryService, times(1)).saveEntry(testJournalEntry);
    }


    @Test
    void testUpdateJournalEntryById_entryNotOwned_returnsNotFound() {
        JournalEntry newDetails = new JournalEntry(); // Details don't matter here
        // User's entry list is empty
        when(userService.findByUserName(testUsername)).thenReturn(testUser);

        ResponseEntity<?> response = journalController.updateJournalEntryById(testEntryId, newDetails);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).findByUserName(testUsername);
        verify(journalEntryService, never()).getJournalEntryById(any(ObjectId.class));
        verify(journalEntryService, never()).saveEntry(any(JournalEntry.class));
    }

    @Test
    void testUpdateJournalEntryById_ownedButServiceReturnsEmpty_returnsNotFound() {
        JournalEntry newDetails = new JournalEntry();
        testUser.getJournalEntries().add(testJournalEntry); // User "owns" by ID
        when(userService.findByUserName(testUsername)).thenReturn(testUser);
        when(journalEntryService.getJournalEntryById(testEntryId)).thenReturn(Optional.empty()); // But service can't find it

        ResponseEntity<?> response = journalController.updateJournalEntryById(testEntryId, newDetails);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).findByUserName(testUsername);
        verify(journalEntryService, times(1)).getJournalEntryById(testEntryId);
        verify(journalEntryService, never()).saveEntry(any(JournalEntry.class));
    }
}
