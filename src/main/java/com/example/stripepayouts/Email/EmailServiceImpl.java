package com.example.stripepayouts.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}") private String sender;

    public String sendEmail(EmailDetails details) {
        try {
            // Creates a simple mail message
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(sender);
            message.setTo(details.getRecipient());
            message.setText(details.getMsgBody());
            message.setSubject(details.getSubject());

            javaMailSender.send(message);

            return "Email Sent";

        } catch (Exception e) {
            return "Error while sending mail: " + e.getMessage();
        }
    }
}
