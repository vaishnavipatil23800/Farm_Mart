package com.farmmart.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

// replaces: models/cartproduct.model.js
@Data
@Document(collection = "cartproducts")
public class CartProduct {

    @Id
    private String id;

    // replaces: productId: { type: mongoose.Schema.ObjectId, ref: 'product' }
    private String productId;

    private int quantity = 1;

    // replaces: userId: { type: mongoose.Schema.ObjectId, ref: 'User' }
    private String userId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
