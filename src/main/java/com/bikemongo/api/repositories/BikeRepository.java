package com.bikemongo.api.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.bikemongo.api.model.Bike;

@Repository
public interface BikeRepository extends MongoRepository<Bike, String> {
	
	Bike findByEmail(String email);
	List<Bike> findAllByEmail(String email);

	Boolean existsByEmail(String email);


}
