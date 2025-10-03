package com.example.stripepayouts.Controller;

import com.example.stripepayouts.DTO.OrderlineDTO;
import com.example.stripepayouts.DTO.PaymentIntentDTO;
import com.example.stripepayouts.DTO.WebHookDTO;
import com.example.stripepayouts.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/webhook")
public class WebHookController {
    private static final Logger log = LoggerFactory.getLogger(WebHookController.class);

    private final PaymentService paymentService;

    public WebHookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Handle incoming webhook from Infigo
     * @param payload
     * @return PaymentIntentDTO with status code 200 if successful, 400 if not processed, 502 if Stripe capture fails
     */
    @PostMapping("/infigo")
    public ResponseEntity<PaymentIntentDTO> handleWebhook(@RequestBody WebHookDTO payload) {
        if (payload.getData() == null || payload.getData().isEmpty()) {
            log.warn("Webhook received with empty data array.");
            return ResponseEntity.badRequest().build();
        }

        OrderlineDTO orderline = payload.getData().get(0); // We expect only one orderline

        try {
            PaymentIntentDTO paymentResult = paymentService.processPayment(orderline);

            if (paymentResult == null) {
                log.info("Orderline ID={} was ignored or invalid for payment.", orderline.getId());
                return ResponseEntity.badRequest().build();
            }

            log.info("Stripe capture successful for Orderline ID={}", orderline.getId());
            return ResponseEntity.ok(paymentResult);
        } catch (Exception e) {
            log.error("Stripe payment capture failed for Orderline ID={}", orderline.getId(), e);
            return ResponseEntity.status(502).body(null);
        }
    }
}
