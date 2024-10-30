package com.farmmart.repository;

import com.farmmart.model.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

// replaces: AddressModel queries
public interface AddressRepository extends MongoRepository<Address, String> {

    // replaces: AddressModel.find({ userId, status: true })
    List<Address> findByUserIdAndStatusTrue(String userId);

    // replaces: AddressModel.find({ userId })
    List<Address> findByUserId(String userId);
}
