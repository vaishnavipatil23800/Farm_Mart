package com.farmmart.repository;

import com.farmmart.model.CartProduct;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

// replaces: CartProductModel.find({ userId }), CartProductModel.findOne({ userId, productId })
public interface CartProductRepository extends MongoRepository<CartProduct, String> {

    // replaces: CartProductModel.find({ userId: userId }).populate('productId')
    List<CartProduct> findByUserId(String userId);

    // replaces: CartProductModel.findOne({ userId, productId })
    Optional<CartProduct> findByUserIdAndProductId(String userId, String productId);

    // replaces: CartProductModel.deleteOne({ _id, userId })
    void deleteByIdAndUserId(String id, String userId);

    // replaces: CartProductModel.deleteMany({ userId })
    void deleteAllByUserId(String userId);
}
