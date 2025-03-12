package com.learning.journalApp.scheduler;

import com.learning.journalApp.cache.AppCache;
import com.learning.journalApp.entity.JournalEntry;
import com.learning.journalApp.entity.User;
import com.learning.journalApp.enums.Sentiment;
import com.learning.journalApp.repository.UserRepositoryImpl;
import com.learning.journalApp.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserScheduler implements SchedulingConfigurer {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AppCache appCache;

    /**
     * Fetches users and sends sentiment analysis emails.
     * <p>
     * This method is scheduled to run at 9 AM on the 10th day of every month.
     * It fetches users who require sentiment analysis, processes their journal entries
     * from the past week, performs sentiment analysis, and sends the results via email.
     * <p>
     * The method works as follows:
     * <ul>
     *   <li>Fetches users who require sentiment analysis from the repository.</li>
     *   <li>For each user, retrieves their journal entries from the past week.</li>
     *   <li>Performs sentiment analysis on the journal entries to determine the most frequent sentiment.</li>
     *   <li>Sends an email to the user with the most frequent sentiment for the past week.</li>
     * </ul>
     */
    @Scheduled(cron = "0 0 9 10 * ?")
    public void fetchUserAndSendSAEmail() {

        List<User> userBySA = userRepository.findUserBySA();
        log.info("Fetched {} users for sentiment analysis", userBySA.size());

        for (User user : userBySA) {
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream()
                    .filter(x -> x.getDate().isAfter(LocalDateTime.now().minusDays(7)))
                    .map(JournalEntry::getSentiment)
                    .toList();

            Map<Sentiment, Integer> sentimentCount = new HashMap<>();
            for (Sentiment sentiment : sentiments) {
                if (sentiment != null) {
                    sentimentCount.put(sentiment, sentimentCount.getOrDefault(sentiment, 0) + 1);
                }
            }
            log.info("Sentiment count for user {}: {}", user.getUserName(), sentimentCount);

            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry : sentimentCount.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }
            log.info("Most frequent sentiment for user {}: {}", user.getUserName(), mostFrequentSentiment);

            if (mostFrequentSentiment != null) {
                emailService.sendMail(user.getEmail(), "Sentiment for last month", mostFrequentSentiment.toString());
                log.info("Sent email to user: {}", user.getUserName());
            }
        }

    }

    /**
     * Configures the scheduled tasks with a custom ThreadPoolTaskScheduler.
     * <p>
     * This method sets up a ThreadPoolTaskScheduler with a pool size of 10 and a custom thread name prefix.
     * The ThreadPoolTaskScheduler helps in managing the concurrent execution of scheduled tasks,
     * providing better performance and resource utilization by reusing threads and controlling
     * the number of concurrent tasks.
     *
     * @param taskRegistrar the ScheduledTaskRegistrar to configure
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setThreadNamePrefix("UserScheduler-");
        taskScheduler.initialize();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }

    /**
     * Clears the application cache.
     * <p>
     * This method is scheduled to run every 10 minutes.
     * It initializes the application cache, effectively clearing any existing cache data.
     */
    @Scheduled(cron = "0 0/10 * ? * *")
    public void clearAppCache() {
        // Method to clear the application cache
        appCache.init();
    }
}
