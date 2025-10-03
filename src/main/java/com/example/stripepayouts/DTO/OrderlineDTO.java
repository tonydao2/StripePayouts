package com.example.stripepayouts.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// This is from lombok and jackson
// It generates getters, setters, toString, equals, and hashCode methods
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderlineDTO {
    // Maps the JSON property "Id" to the Java field "id"
    @JsonProperty("Id")
    private String id;

    @JsonProperty("CustomerId")
    private int customerId;

    @JsonProperty("Status")
    private int status;
}
