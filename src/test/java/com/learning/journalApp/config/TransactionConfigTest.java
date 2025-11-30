package com.learning.journalApp.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@SpringBootTest
class TransactionConfigTest {

    @Test
    void testMongoTransactionManagerBean() {
        // Arrange
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TransactionConfig.class);

        // Mock MongoDatabaseFactory for testing
        MongoDatabaseFactory mockMongoDatabaseFactory = mock(MongoDatabaseFactory.class);

        // Get the bean from the context and inject the mock factory
        TransactionConfig transactionConfig = context.getBean(TransactionConfig.class);
        MongoTransactionManager mongoTransactionManager = transactionConfig.mongoTransactionManager(mockMongoDatabaseFactory);

        // Assert
        assertNotNull(mongoTransactionManager, "MongoTransactionManager bean should not be null");

        context.close();
    }
}
