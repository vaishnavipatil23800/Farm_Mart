package com.farmmart.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

// replaces: models/address.model.js
@Data
@Document(collection = "addresses")
public class Address {

    @Id
    private String id;

    private String addressLine = "";

    private String city = "";

    private String state = "";

    private String pincode = "";

    private String country = "";

    private Long mobile;

    private boolean status = true;

    // replaces: userId: { type: mongoose.Schema.ObjectId }
    private String userId = "";

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
