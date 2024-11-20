package com.farmmart.repository;

import com.farmmart.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

// replaces: ProductModel.find(), ProductModel.findOne(), etc.
public interface ProductRepository extends MongoRepository<Product, String> {

    // replaces: ProductModel.find({ category: { $in: id } }).limit(15)
    List<Product> findByCategoryIn(List<String> categoryIds, Pageable pageable);

    // replaces: ProductModel.find({ category: $in, subCategory: $in })
    Page<Product> findByCategoryInAndSubCategoryIn(
        List<String> categoryIds,
        List<String> subCategoryIds,
        Pageable pageable
    );

    // replaces: ProductModel.countDocuments(query)
    long countByCategoryIn(List<String> categoryIds);

    // replaces: $text search - using MongoDB text index
    @Query("{ '$text': { '$search': ?0 } }")
    Page<Product> findByTextSearch(String searchText, Pageable pageable);
}
