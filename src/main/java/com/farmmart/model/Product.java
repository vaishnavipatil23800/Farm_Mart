package com.farmmart.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// replaces: models/product.model.js

@Data
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String name;

    // replaces: image: { type: Array, default: [] }
    private List<String> image = new ArrayList<>();

    // replaces: [{ type: mongoose.Schema.ObjectId, ref: 'category' }]
    // In Spring Boot MongoDB, we store the string IDs directly
    private List<String> category = new ArrayList<>();

    private List<String> subCategory = new ArrayList<>();

    private String unit = "";

    private Integer stock;

    private Double price;

    private Double discount;

    private String description = "";

    // replaces: more_details: { type: Object, default: {} }
    private Map<String, Object> moreDetails = new HashMap<>();

    private boolean publish = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
