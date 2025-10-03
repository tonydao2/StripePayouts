package com.example.stripepayouts.Controller;

import com.example.stripepayouts.DTO.OrderlineDTO;
import com.example.stripepayouts.DTO.PaymentIntentDTO;
import com.example.stripepayouts.Service.InfigoService;
import com.example.stripepayouts.Service.PaymentService;
import com.example.stripepayouts.Service.StripeService;
import com.stripe.model.PaymentIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class PaymentController {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Manual Trigger
    @PostMapping("/processPayment")
    public ResponseEntity<PaymentIntentDTO> processPayment(@RequestBody OrderlineDTO orderline) {try {
            PaymentIntentDTO result = paymentService.processPayment(orderline);
            if (result == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(502).build();
        }
    }

}
