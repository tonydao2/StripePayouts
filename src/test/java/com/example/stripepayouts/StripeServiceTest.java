package com.example.stripepayouts.Service;

import com.example.stripepayouts.DTO.OrderDTO;
import com.example.stripepayouts.DTO.OrderlineDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        OrderlineDTO item1 = new OrderlineDTO();
        item1.setId("1");
        item1.setName("12JKGFPP");
        OrderlineDTO item2 = new OrderlineDTO();
        item2.setId("2");
        item2.setName("SPF15951");
        String testPaymentIntentId = "pi_3SIZU1IOwGRLFxh81PsH7smp";
        log.info("Api Key:  " + com.stripe.Stripe.apiKey);

        OrderDTO order = new OrderDTO();
        order.setId(12706L);
        order.setCaptureTransactionId(testPaymentIntentId);
        order.setOrderLineItems(Arrays.asList(item1, item2));
        order.setOrderTotal(42.50);

        try {
            PaymentIntent captured = stripeService.capturePayment(order);

            log.info("PaymentIntent ID: {}", captured.getId());
            log.info("Status: {}", captured.getStatus());

            Charge charge = Charge.retrieve(captured.getLatestCharge());
            log.info("Latest Charge ID: {}", charge.getId());

            // Add metadata to the charge so we can later us during payout to add to description
            Map<String, Object> updateParams = getStringObjectMap(charge, order);
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

    private static @NotNull Map<String, Object> getStringObjectMap(Charge charge, OrderDTO order) {
        Map<String, String> newMetadata = new HashMap<>(charge.getMetadata());
        newMetadata.put("orderId", order.getId().toString());
        newMetadata.put("orderTotal", String.valueOf(order.getOrderTotal()));

        // Add names of all items in the order to metadata
        StringBuilder itemNames = new StringBuilder();
        for (OrderlineDTO item : order.getOrderLineItems()) {
            String name = item.getName();
            if (name != null && !name.isEmpty()) {
                if (!itemNames.isEmpty()) {
                    itemNames.append(", ");
                }
                itemNames.append(name);
            }
        }

        newMetadata.put("itemNames", itemNames.toString());

        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("metadata", newMetadata);
        return updateParams;
    }
}
