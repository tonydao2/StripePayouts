package com.example.stripepayouts.Service;

import com.example.stripepayouts.Controller.PaymentController;
import com.example.stripepayouts.DTO.OrderDTO;
import com.example.stripepayouts.DTO.OrderlineDTO;
import com.example.stripepayouts.DTO.PaymentIntentDTO;
import com.stripe.model.PaymentIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final StripeService stripeService;
    private final InfigoService infigoService;

    public PaymentService(StripeService stripeService, InfigoService infigoService) {
        this.stripeService = stripeService;
        this.infigoService = infigoService;
    }

    public PaymentIntentDTO processPayment(OrderlineDTO orderline) {
        if (orderline.getStatus() != 23) {
            // Only process if status is Approved (23) else ignore
            log.info("Ignoring orderline with status {}. Only processing status 23 (Approved).", orderline.getStatus());
            return null;
        }

        log.info("Processing orderline with status {}.", orderline.getStatus());
        OrderDTO order = infigoService.getOrder(orderline);
        if (order == null) {
            log.warn("No order found for orderline with CustomerId={}", orderline.getCustomerId());
            return null;
        }

        log.info("Found order with ID={} for orderline with CustomerId={}", order.getId(), orderline.getCustomerId());

        if (order.getCaptureTransactionId() == null) {
            log.warn("Order with ID={} does not have a CaptureTransactionId. Cannot process payment.", order.getId());
            return null;
        }

        try {
            PaymentIntent paymentIntent = stripeService.capturePayment(order.getCaptureTransactionId());
            return new PaymentIntentDTO(paymentIntent);
        } catch (RuntimeException e) {
            log.error("Stripe capture failed for transactionId={}", order.getCaptureTransactionId(), e);
            throw e;
        }
    }
}
