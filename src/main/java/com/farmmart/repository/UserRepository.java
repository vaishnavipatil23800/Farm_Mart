package com.farmmart.repository;

import com.farmmart.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

// replaces: UserModel.findOne({ email }) and all other UserModel queries
// MongoRepository gives you: save(), findById(), findAll(), deleteById() for FREE
// You just add custom methods below

public interface UserRepository extends MongoRepository<User, String> {

    // replaces: UserModel.findOne({ email })
    Optional<User> findByEmail(String email);

    // replaces: UserModel.findOne({ email }) and checking if result exists
    boolean existsByEmail(String email);
}
