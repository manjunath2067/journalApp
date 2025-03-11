package com.learning.journalApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendEmail() {
        String to = "test@gmail.com";
        String subject = "Sending Mail via JavaMailSender";
        String body = "How are you doing?";

        emailService.sendMail(to, subject, body);

        verify(javaMailSender, times(1)).send(new SimpleMailMessage() {{
            setTo(to);
            setSubject(subject);
            setText(body);
        }});
    }

    @Test
    public void testSendEmailException() {
        String to = "test@gmail.com";
        String subject = "Sending Mail via JavaMailSender";
        String body = "How are you doing?";

        doThrow(new RuntimeException("Mail server error")).when(javaMailSender).send(new SimpleMailMessage());

        emailService.sendMail(to, subject, body);

        // Verify that the send method was called and exception was handled
        verify(javaMailSender, times(1)).send(new SimpleMailMessage() {{
            setTo(to);
            setSubject(subject);
            setText(body);
        }});
    }
}