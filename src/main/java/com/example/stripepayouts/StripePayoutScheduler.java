package com.example.stripepayouts;

import com.example.stripepayouts.Email.EmailDetails;
import com.example.stripepayouts.Service.StripeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.example.stripepayouts.Email.EmailServiceImpl;

@Component
public class StripePayoutScheduler {

    private static final Logger log = LoggerFactory.getLogger(StripePayoutScheduler.class);
    private final EmailServiceImpl emailService;
    private final StripeService stripeService;

    public StripePayoutScheduler(EmailServiceImpl emailService, StripeService stripeService) {
        this.emailService = emailService;
        this.stripeService = stripeService;
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = ("0 * * * * *")) // Every minute for testing
    public void runEveryMinute() {
        log.info("Running every minute task at {}", dateFormat.format(new Date()));
    }

    @Scheduled(cron = ("0 0 8 * * 1-5")) // At 09:00 AM, Monday through Friday we run this
    public void runDailyPayoutTask() {
        log.info("Running daily payout task at {}", dateFormat.format(new Date()));

        // TODO: Implement logic to check database everyday at 8 am to see if there are any available payouts at this time


        // Triggers the payout process every day at 8 AM
        stripeService.processPayout();


        EmailDetails details = new EmailDetails(
                "tdao@sbpack.com",
                "Daily Payout Report",
                "This is a test email sent from the Stripe Payout Scheduler.",
                null
        );

        try {
            String response = emailService.sendEmail(details);
            log.info("Email service response: {}", response);
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage());
        }


    }

}
