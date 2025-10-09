package com.example.stripepayouts.Service;

import com.example.stripepayouts.DTO.OrderDTO;
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
     * @param order OrderDTO containing order details including captureTransactionId
     * @return PaymentIntent object after capturing the payment
     * @throws RuntimeException if Stripe API call fails
     */
    // probably need to pass in OrderDTO to add metadata instead of just captureTransactionId
    public PaymentIntent capturePayment(OrderDTO order) {
        // TODO: Add metadata to the payment intent with orderId and customerId
        try {
            log.info("Attempting to capture payment with ID: {}", order.getCaptureTransactionId());
            PaymentIntent intent = PaymentIntent.retrieve(order.getCaptureTransactionId());
            log.info("Successfully retrieved PaymentIntent: {}", intent.getId());
            return intent.capture();
        } catch (StripeException e) {
            log.error("Failed to retrieve PaymentIntent for ID: {}", order.getCaptureTransactionId(), e);
            throw new RuntimeException("Stripe payment capture failed", e);
        }
    }
}
