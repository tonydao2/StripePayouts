package com.example.stripepayouts.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    @Value("${STRIPE_API_KEY}")
    private String stripeApiKey;

    @Value("${STRIPE_API_BASE_URL}")
    private String stripeApiBaseUrl;

    /**
     * Captures payment for the given order using Stripe API
     * @param captureTransactionId The capture transaction ID from the order
     */
    public void capturePayment(String captureTransactionId) {
        // Logic to capture payment using Stripe API
        try {

        }


        System.out.println("Capturing payment for order: " + order);
    }
}
