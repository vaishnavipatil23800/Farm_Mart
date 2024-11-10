package com.farmmart.repository;

import com.farmmart.model.SubCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

// replaces: subCategoryModel queries
public interface SubCategoryRepository extends MongoRepository<SubCategory, String> {

    // replaces: SubCategoryModel.find({ category: { $in: ids } })
    List<SubCategory> findByCategoryIn(List<String> categoryIds);
}
