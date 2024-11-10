package com.farmmart.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// replaces: models/category.model.js
@Data
@Document(collection = "categories")
public class Category {

    @Id
    private String id;

    private String name;

    private String image;
}
