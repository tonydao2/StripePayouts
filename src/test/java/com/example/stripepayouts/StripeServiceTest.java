package com.example.stripepayouts.Service;

import com.example.stripepayouts.DTO.OrderDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class StripeServiceTest {

    public static StripeService stripeService;
    public static final Logger log = LoggerFactory.getLogger(StripeServiceTest.class);

    @BeforeEach
    void setup() {
        stripeService = new StripeService();

        com.stripe.Stripe.apiKey = System.getenv("STRIPE_API_KEY");
        log.info("API KEY: {}", com.stripe.Stripe.apiKey);
    }

    @Test
    void testStripeApiKeyLoaded() {
        String apiKey = System.getenv("STRIPE_API_KEY");
        log.info("Loaded STRIPE_API_KEY: {}", apiKey);
        assert apiKey != null && !apiKey.isEmpty();
    }

    @Test
    void testCapturePayment() {
        // Replace with a real test PaymentIntent ID from Stripe
        String testPaymentIntentId = "pi_3SICDdIOwGRLFxh81b6w5EYy";
        log.info("Api Key:  " + com.stripe.Stripe.apiKey);

        OrderDTO order = new OrderDTO();
        order.setCaptureTransactionId(testPaymentIntentId);

        try {
            PaymentIntent captured = stripeService.capturePayment(order);

            log.info("PaymentIntent ID: {}", captured.getId());
            log.info("Status: {}", captured.getStatus());

            Charge charge = Charge.retrieve(captured.getLatestCharge());
            log.info("Latest Charge ID: {}", charge.getId());

            // Add metadata to the charge so we can later us during payout to add to description
            Map<String, String> newMetadata = new HashMap<>(charge.getMetadata());
            newMetadata.put("orderId", "233322");
            newMetadata.put("orderTotal", "99.99");

            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("metadata", newMetadata);
            charge = charge.update(updateParams);

            // TODO: Store orderId and orderTotal and availableOn in database for future reference
            // Here we are trying to get the balance transaction to get available on date
            String balanceTransactionId = charge.getBalanceTransaction();
            // This contains available on and amount
            BalanceTransaction balanceTransaction = BalanceTransaction.retrieve(balanceTransactionId);
            Long availableOn = balanceTransaction.getAvailableOn(); // Store this in database
            log.info("Balance Transaction ID: {}, Available On: {}", balanceTransactionId, availableOn);

            // Convert availableOn (epoch seconds) to ZonedDateTime
            Instant availableInstant = Instant.ofEpochSecond(availableOn);
            log.info("Available Instant: {}", availableInstant);
            ZonedDateTime availableDateTime = availableInstant.atZone(ZoneId.systemDefault());
            log.info("Available On: {}", availableDateTime);

        } catch (StripeException e) {
            log.error("Stripe API error: {} - {}", e.getStatusCode(), e.getMessage());
        } catch (RuntimeException e) {
            log.error("Runtime error: {}", e.getMessage());
        }
    }
}
