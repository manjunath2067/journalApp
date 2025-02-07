package com.learning.journalApp.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class  JournalEntityControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private UserService userService;

    @GetMapping("{userName}")
    public ResponseEntity<?> getAll(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        List<JournalEntry> entryAll = user.getJournalEntries();
        if(entryAll != null && !entryAll.isEmpty()) {
            return new ResponseEntity<>(entryAll,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("{userName}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry entry, @PathVariable String userName){
        try {
            entry.setDate(LocalDateTime.now());
            journalEntryService.saveEntry(entry,userName);
            return new ResponseEntity<>(entry,HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) {
        Optional<JournalEntry> journalEntry = journalEntryService.getJournalEntryById(myId);
        return journalEntry.map(entry -> new ResponseEntity<>(entry, HttpStatus.OK))
              .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("id/{userName}/{deleteId}")
    public ResponseEntity<?> deleteEntryById(@PathVariable ObjectId deleteId, @PathVariable String userName) {
        journalEntryService.deleteJournalEntryById(deleteId, userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("id/{userName}/{id}")
    public ResponseEntity<?> updateJournalEntryById(
          @PathVariable ObjectId id,
          @RequestBody JournalEntry newEntry,
          @PathVariable String userName
    )
    {
        JournalEntry old = journalEntryService.getJournalEntryById(id).orElse(null);
        if (old != null) {
            old.setTitle(!newEntry.getTitle().isEmpty()
                  ? newEntry.getTitle()
                  : old.getTitle());
            old.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty()
                  ? newEntry.getContent()
                  : old.getContent());
            journalEntryService.saveEntry(old);
            return new ResponseEntity<>(old, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
