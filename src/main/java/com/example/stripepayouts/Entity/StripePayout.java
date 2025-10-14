package com.example.stripepayouts.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "StripePayouts")
public class StripePayout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "balance_availible_on")
    private LocalDateTime balanceAvailableOn;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "order_total")
    private Double orderTotal;

    @Column(name = "customer_id")
    private Integer customerId;

    public StripePayout() {}

    public StripePayout(LocalDateTime balanceAvailableOn, Integer orderId, Double orderTotal, Integer customerId) {
        this.balanceAvailableOn = balanceAvailableOn;
        this.orderId = orderId;
        this.orderTotal = orderTotal;
        this.customerId = customerId;
    }

    public Long getId() { return id; }
    public LocalDateTime getBalanceAvailable() { return balanceAvailableOn; }
    public void setBalanceAvailable(LocalDateTime balanceAvailable) { this.balanceAvailableOn = balanceAvailable; }
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Double getOrderAmount() { return orderTotal; }
    public void setOrderAmount(Double orderAmount) { this.orderTotal = orderAmount; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

}
