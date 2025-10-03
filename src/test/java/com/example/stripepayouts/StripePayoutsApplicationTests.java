/*
package com.example.stripepayouts;

import com.example.stripepayouts.Controller.PaymentController;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.stripepayouts.DTO.InfigoDTO;
import com.example.stripepayouts.DTO.PaymentIntentDTO;
import com.example.stripepayouts.Service.StripeService;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class) // Tests only the PaymentController
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Mock the service that the controller depends on
    private StripeService stripeService;

    @Test
    void processPayment_shouldReturnOk_whenPaymentCaptured() throws Exception {
        // Mock Stripe response
        PaymentIntent mockIntent = new PaymentIntent();
        mockIntent.setId("pi_test123");
        Mockito.when(stripeService.capturePayment(anyString())).thenReturn(mockIntent);

        // JSON body like InfigoDTO
        String requestBody = """
            {
              "Id": 123,
              "CaptureTransactionId": "pi_test123"
            }
            """;

        mockMvc.perform(post("/api/payment/processPayment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pi_test123")); // PaymentIntentDTO exposes id
    }

    @Test
    void processPayment_shouldReturnBadRequest_whenMissingFields() throws Exception {
        String requestBody = """
            {
              "Id": 123
            }
            """;

        mockMvc.perform(post("/api/payment/processPayment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}*/
