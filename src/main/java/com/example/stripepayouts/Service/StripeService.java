package com.example.stripepayouts.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCaptureParams;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    private static final Logger log = LoggerFactory.getLogger(StripeService.class);

    /**
     * Captures payment for the given order using Stripe API
     * @param captureTransactionId The capture transaction ID from the order
     */
    public PaymentIntent capturePayment(String captureTransactionId) {
        try {
            log.info("Attempting to capture payment with ID: {}", captureTransactionId);
            PaymentIntent intent = PaymentIntent.retrieve(captureTransactionId);
            log.info("Successfully retrieved PaymentIntent: {}", intent.getId());
            return intent.capture();
        } catch (StripeException e) {
            log.error("Failed to retrieve PaymentIntent for ID: {}", captureTransactionId, e);
            throw new RuntimeException("Stripe payment capture failed", e);
        }
    }
}
