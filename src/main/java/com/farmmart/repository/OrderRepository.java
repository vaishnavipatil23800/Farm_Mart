package com.farmmart.repository;

import com.farmmart.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

// replaces: OrderModel queries
public interface OrderRepository extends MongoRepository<Order, String> {

    // replaces: OrderModel.find({ userId })
    List<Order> findByUserId(String userId);
}
