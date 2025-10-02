package com.example.stripepayouts.Controller;

import com.example.stripepayouts.DTO.InfigoDTO;
import com.example.stripepayouts.Service.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller to handle payment processing requests
 * Infigo will have a trigger that calls this controller when an order is approved.
 * It will call the controller and contain the JSON payload with everything.
 * We need to get the CaptureTransactionId to call Stripe and capture the payment.
 * We then call Siteflow to mark the order as paid.
 */
@RestController
@RequestMapping("/api/payment")
class PaymentController {
    private final StripeService stripeService;

    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/processPayment")
    public ResponseEntity<String> processPayment(@RequestBody InfigoDTO order) {
        // Call the StripeService to process the payment using the capture ID from the order
        stripeService.capturePayment(order.getCaptureTransactionId());

        return ResponseEntity.ok("Payment processed successfully for " + order.getId());
    }


}
