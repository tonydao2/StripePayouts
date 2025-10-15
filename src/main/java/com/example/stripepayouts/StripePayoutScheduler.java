package com.example.stripepayouts;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class StripePayoutScheduler {

    private static final Logger log = LoggerFactory.getLogger(StripePayoutScheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = ("0 6 12 * * 1-5")) // At 08:00 AM, Monday through Friday we run this
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
    }

    @Scheduled(cron = ("0 * * * * *")) // Every minute for testing
    public void runEveryMinute() {
        log.info("Running every minute task at {}", dateFormat.format(new Date()));
    }

    @Scheduled(cron = ("0 0 9 * * 1-5")) // At 09:00 AM, Monday through Friday we run this
    public void runDailyPayoutTask() {

    }

}
