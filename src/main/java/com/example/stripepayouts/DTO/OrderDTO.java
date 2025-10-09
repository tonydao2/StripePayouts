package com.example.stripepayouts.DTO;
import com.example.stripepayouts.DTO.OrderlineDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

// This is from lombok and jackson
// It generates getters, setters, toString, equals, and hashCode methods
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    // Maps the JSON property "Id" to the Java field "id"
    @JsonProperty("Id")
    private Long id;

    // Maps the JSON property "CaptureTransactionId" to the Java field "captureTransactionId"
    @JsonProperty("CaptureTransactionId")
    private String captureTransactionId;

    @JsonProperty("OrderTotal")
    private double orderTotal; // Infigo API returns OrderTotal as an integer but we can use double for more flexibility

    @JsonProperty("OrderLineItems")
    private List<String> orderlineitems;
}
