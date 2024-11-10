package com.farmmart.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

// replaces: models/subCategory.model.js
@Data
@Document(collection = "subcategories")
public class SubCategory {

    @Id
    private String id;

    private String name;

    private String image;

    // replaces: [{ type: mongoose.Schema.ObjectId, ref: 'category' }]
    private List<String> category = new ArrayList<>();
}
