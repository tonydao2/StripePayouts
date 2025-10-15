package com.example.stripepayouts.Service;

import com.example.stripepayouts.DTO.OrderDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.BalanceTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {
    private static final Logger log = LoggerFactory.getLogger(StripeService.class);

    /**
     * Captures payment for the given order using Stripe API
     * @param order OrderDTO containing order details including captureTransactionId
     * @return PaymentIntent object after capturing the payment
     * @throws RuntimeException if Stripe API call fails
     */
    public PaymentIntent capturePayment(OrderDTO order) throws StripeException {
        try {
            log.info("Attempting to capture payment with ID: {}", order.getCaptureTransactionId());

            PaymentIntent intent = PaymentIntent.retrieve(order.getCaptureTransactionId());
            log.info("Successfully retrieved PaymentIntent: {}", intent.getId());
            PaymentIntent captured = intent.capture();

            // This gets the charge id we just captured
            Charge charge = Charge.retrieve(captured.getLatestCharge());
            log.info("Charge ID: {}", charge.getId());

            // Add metadata to the charge so we can later us during payout to add to description
            Map<String, String> newMetadata = new HashMap<>(charge.getMetadata());
            newMetadata.put("orderId", order.getId().toString());
            newMetadata.put("orderTotal", String.valueOf(order.getOrderTotal()));

            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("metadata", newMetadata);
            charge = charge.update(updateParams);

            // TODO: Store orderId and orderTotal and availableOn in database for future reference
            // Here we are trying to get the balance transaction to get available on date
            String balanceTransactionId = charge.getBalanceTransaction();
            // This contains available on and amount
            BalanceTransaction balanceTransaction = BalanceTransaction.retrieve(balanceTransactionId);
            Long availableOn = balanceTransaction.getAvailableOn(); // Store this in database


            return captured;
        } catch (com.stripe.exception.InvalidRequestException e) {
            if ("payment_intent_unexpected_state".equals(e.getCode())) {
                log.warn("Payment already captured for ID: {}", order.getCaptureTransactionId());
                return PaymentIntent.retrieve(order.getCaptureTransactionId());
            } else {
                throw e;
            }
        } catch (StripeException e) {
            log.error("Failed to retrieve PaymentIntent for ID: {}", order.getCaptureTransactionId(), e);
            throw new RuntimeException("Stripe payment capture failed", e);
        }
    }

    /***
     * Placeholder for future payout functionality
     * @return null for now
     * @throws StripeException if Stripe API call fails
     *
     * Idea is to create a webhook that listens for balance.available events from Stripe
     * When we get that event we can check our database for any orders that have availableOn
     * date that is less than or equal to the current date and if status on DB is not paid out
     * then we can create a payout to our connected account with the metadata from the charge
     * so we can add orderId and orderTotal to the description of the payout
     */
    public void Payout() {
        // To be implemented

    }
}
