package com.learning.journalApp.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.journalApp.entity.JournalEntry;
import com.learning.journalApp.entity.User;
import com.learning.journalApp.service.JournalEntryService;
import com.learning.journalApp.service.UserService;

@RestController
@RequestMapping("/journal")
public class JournalEntityControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private UserService userService;

    /**
     * Retrieves all journal entries for the authenticated user.
     *
     * @return a ResponseEntity with the list of journal entries and HTTP status OK if found, or HTTP status NOT_FOUND
     *       if no journal entries are found
     */
    @GetMapping
    public ResponseEntity<?> getAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> entryAll = user.getJournalEntries();
        if (entryAll != null && !entryAll.isEmpty()) {
            return new ResponseEntity<>(entryAll, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a new journal entry.
     *
     * @param entry the journal entry to create
     * @return a ResponseEntity with the created journal entry and HTTP status CREATED if successful, or HTTP status
     *       BAD_REQUEST if there is an error during creation
     */
    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry entry)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            entry.setDate(LocalDateTime.now());
            journalEntryService.saveEntry(entry, userName);
            return new ResponseEntity<>(entry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves a journal entry by its ID.
     *
     * @param myId the ID of the journal entry to retrieve
     * @return a ResponseEntity with the journal entry and HTTP status OK if found, or HTTP status NOT_FOUND if the
     *       journal entry is not found
     */
    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).toList();
        if (!collect.isEmpty()) {
            Optional<JournalEntry> journalEntry = journalEntryService.getJournalEntryById(myId);
            if (journalEntry.isPresent()) {
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Deletes a journal entry by its ID.
     *
     * @param deleteId the ID of the journal entry to delete
     * @return a ResponseEntity with HTTP status NO_CONTENT if the deletion is successful, or HTTP status NOT_FOUND if
     *       the journal entry is not found
     */
    @DeleteMapping("id/{deleteId}")
    public ResponseEntity<?> deleteEntryById(
          @PathVariable ObjectId deleteId
    )
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean removed = journalEntryService.deleteJournalEntryById(deleteId, userName);
        if (removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Updates a journal entry by its ID.
     *
     * @param id       the ID of the journal entry to update
     * @param newEntry the new journal entry details
     * @return a ResponseEntity with the updated journal entry and HTTP status OK if the update is successful, or HTTP
     *       status NOT_FOUND if the journal entry is not found
     */
    @PutMapping("id/{id}")
    public ResponseEntity<?> updateJournalEntryById(
          @PathVariable ObjectId id,
          @RequestBody JournalEntry newEntry
    )
    {
        // Get the authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        // Find the user by username
        User user = userService.findByUserName(userName);

        // Check if the user has a journal entry with the given ID
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(id)).toList();
        if (!collect.isEmpty()) {

            // If the journal entry exists, retrieve it from the database
            Optional<JournalEntry> journalEntry = journalEntryService.getJournalEntryById(id);
            if (journalEntry.isPresent()) {

                // Update the journal entry with new details
                JournalEntry old = journalEntry.get();
                old.setTitle(!newEntry.getTitle().isEmpty() ? newEntry.getTitle() : old.getTitle());
                old.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty()
                      ? newEntry.getContent()
                      : old.getContent());

                // Save the updated journal entry
                journalEntryService.saveEntry(old);
                return new ResponseEntity<>(old, HttpStatus.OK);
            }
        }
        // Return NOT_FOUND if the journal entry does not exist
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
