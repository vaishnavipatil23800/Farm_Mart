package com.farmmart.repository;

import com.farmmart.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

// replaces: CategoryModel queries
public interface CategoryRepository extends MongoRepository<Category, String> {
    // MongoRepository already provides: findAll(), save(), deleteById(), findById()
    // No extra methods needed for basic CRUD
}
