package com.example.stripepayouts.Controller;

import com.example.stripepayouts.DTO.InfigoDTO;
import com.example.stripepayouts.DTO.PaymentIntentDTO;
import com.example.stripepayouts.Service.StripeService;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<PaymentIntentDTO> processPayment(@RequestBody InfigoDTO order) {
        // Call the StripeService to process the payment using the capture ID from the order
        if (order.getId() == null || order.getCaptureTransactionId() == null) {
            return ResponseEntity.badRequest().build();
        }

        PaymentIntent paymentIntent = stripeService.capturePayment(order.getCaptureTransactionId());

        if (paymentIntent == null) {
            return ResponseEntity.status(502).build(); // Bad Gateway if Stripe call fails
        } else {
            return ResponseEntity.ok(new PaymentIntentDTO(paymentIntent)); // Return the payment intent details
        }
    }

}
