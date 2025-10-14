package com.example.stripepayouts.Service;

import com.example.stripepayouts.DTO.OrderlineDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import com.example.stripepayouts.DTO.OrderDTO;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class InfigoService {
    private static final Logger log = LoggerFactory.getLogger(InfigoService.class);

    @Value("${INFIGO_BASE_URL}")
    private String infigoBaseUrl;

    @Value("${INFIGO_API_KEY}")
    private String infigoApiKey;

    private RestClient restClient;
    private final RestClient.Builder builder;

    @Autowired
    public InfigoService(RestClient.Builder builder) {
        this.builder = builder;
    }

    @PostConstruct // Initialize RestClient after properties are set or else it will be null
    public void init() {
        this.restClient = builder
                .baseUrl(infigoBaseUrl)
                .defaultHeader("Authorization", "Basic " + infigoApiKey)
                .build();
    }


    /**
     * Fetches the order containing the specified orderline from Infigo API
     * @param orderline The orderline to search for
     * @return The OrderDTO containing the orderline, or null if not found
     */
    public OrderDTO getOrder(OrderlineDTO orderline) {
        // We are given an orderline, which has a CustomerId
        // Use that CustomerId to call the Infigo API to get all orders for that customer
        // https://superiorpackaging.infigosoftware.com/services/api/order?CustomerId={id}
        // Go through said orders and find the one that has the orderline we are looking for
        try {
            log.info("Fetching orders for CustomerId={}", orderline.getCustomerId());

            // Step 1: fetch order IDs for the customer
            List<Integer> orderIds = restClient.get()
                    .uri("/order?CustomerId={id}", orderline.getCustomerId())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Integer>>() {});

            log.debug("Order IDs response: {}", orderIds);

            if (orderIds == null) {
                log.warn("No orders found for CustomerId={}", orderline.getCustomerId());
                return null; // No orders found for this customer
            }

            // Step 2: iterate through orders, fetch details, and check for matching line
            for (Integer orderId : orderIds) {
                log.info("Fetching details for OrderId={}", orderId);
                OrderDTO order = restClient.get()
                        .uri("/order/detail/{id}", orderId)
                        .retrieve()
                        .body(OrderDTO.class);


                if (order != null && order.getOrderLineItems() != null) {
                    log.debug("Order {} contains {} orderlines", orderId, order.getOrderLineItems().size());

                    boolean match = order.getOrderLineItems().stream()
                            .anyMatch(line -> line.equals(orderline.getId().toString()));

                    if (match) {
                        log.info("Match found: OrderId={} contains OrderlineId={}", orderId, orderline.getId());
                        return order; // ✅ Found the order containing the orderline
                    }
                }
            }

            return null; // No matching order found
        } catch (Exception e) {
            log.error("Failed to get order from Infigo for CustomerId={}: {}", orderline.getCustomerId(), e.getMessage());
            return null;
        }

    }

}
