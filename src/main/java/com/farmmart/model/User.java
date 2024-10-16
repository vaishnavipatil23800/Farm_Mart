package com.farmmart.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// @Document tells Spring this is a MongoDB collection
// replaces: const userSchema = new mongoose.Schema({...})
// replaces: const UserModel = mongoose.model("User", userSchema)

@Data           // Lombok: auto-generates getters, setters, toString
@Document(collection = "users")
public class User {

    @Id         // MongoDB _id field
    private String id;

    private String name;

    @Indexed(unique = true)  // replaces: unique: true in mongoose
    private String email;

    private String password;

    private String avatar = "";

    private Long mobile;

    private String refreshToken = "";

    private boolean verifyEmail = false;

    private Instant lastLoginDate;

    // replaces: enum: ["Active", "Inactive", "Suspended"]
    private String status = "Active";

    // replaces: [{ type: mongoose.Schema.ObjectId, ref: 'address' }]
    private List<String> addressDetails = new ArrayList<>();

    private List<String> shoppingCart = new ArrayList<>();

    private List<String> orderHistory = new ArrayList<>();

    private String forgotPasswordOtp;

    // FIXED BUG: Using Instant instead of Date - no more string concatenation bug
    private Instant forgotPasswordExpiry;

    // replaces: enum: ['ADMIN', 'USER']
    private String role = "USER";

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
