package com.learning.journalApp.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class represents a configuration entity for the journal app.
 * Using a record provides several advantages:
 * 1. Less boilerplate code: Getters, toString, equals, and hashCode methods are automatically generated.
 * 2. Immutability: Records are immutable by default, leading to safer and more predictable code.
 * 3. Conciseness: Records provide a more concise and readable way to define data-carrying classes.
 */
@Document(collection = "config_journal_app")
public record ConfigJournalAppEntity(String key, String value) {
}