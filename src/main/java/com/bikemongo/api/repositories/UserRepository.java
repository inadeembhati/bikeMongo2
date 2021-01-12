package com.bikemongo.api.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

import com.bikemongo.api.model.User;

public interface UserRepository extends MongoRepository<User, String> {
	
	Optional<User> findByEmail(String email);
	
	Boolean existsByEmail(String email);

}
