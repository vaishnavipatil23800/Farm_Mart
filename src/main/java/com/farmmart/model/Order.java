package com.farmmart.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

// replaces: models/order.model.js
@Data
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String userId;

    private String orderId;

    private String productId;

    // replaces: product_details: { name: String, image: Array }
    private ProductDetails productDetails;

    private String paymentId = "";

    private String paymentStatus = "";

    private String deliveryAddress;

    private Double subTotalAmt = 0.0;

    private Double totalAmt = 0.0;

    private String invoiceReceipt = "";

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // Inner class for product details snapshot
    @Data
    public static class ProductDetails {
        private String name;
        private List<String> image;
    }
}
