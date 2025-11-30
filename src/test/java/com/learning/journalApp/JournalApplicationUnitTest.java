package com.learning.journalApp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class JournalApplicationUnitTest {

    @InjectMocks
    private JournalApplication journalApplication;

    @Mock
    private PlatformTransactionManager platformTransactionManager;

    @Test
    void testTransactionTemplateBeanCreation() {
        // Arrange
        // platformTransactionManager is already mocked by @Mock

        // Act
        TransactionTemplate transactionTemplate = journalApplication.transactionTemplate(platformTransactionManager);

        // Assert
        assertNotNull(transactionTemplate, "TransactionTemplate should not be null");
        assertEquals(platformTransactionManager, transactionTemplate.getTransactionManager(), "TransactionManager should be the mocked one");
        assertEquals(TransactionDefinition.PROPAGATION_REQUIRED, transactionTemplate.getPropagationBehavior(), "Propagation behavior should be PROPAGATION_REQUIRED");
    }
}