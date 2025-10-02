package com.example.stripepayouts.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stripe.model.PaymentIntent;
import lombok.Data;


// Create a DTO for the PaymentIntent response from Stripe
// So we don't send back everything and client secret
@Data
public class PaymentIntentDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("amount_received")
    private Long amountReceived;

    @JsonProperty("customer")
    private String customer;

    @JsonProperty("description")
    private String description;

    // Constructor mapping from Stripe PaymentIntent
    public PaymentIntentDTO(PaymentIntent intent) {
        this.id = intent.getId();
        this.amount = intent.getAmount();
        this.currency = intent.getCurrency();
        this.amountReceived = intent.getAmountReceived();
        this.customer = intent.getCustomer();
        this.description = intent.getDescription();
    }
}
